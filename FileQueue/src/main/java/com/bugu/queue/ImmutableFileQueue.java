package com.bugu.queue;

import com.bugu.queue.bean.FileQueueException;
import com.bugu.queue.converter.Converter;
import com.bugu.queue.converter.GsonConverterFactory;
import com.bugu.queue.header.AbsPointer;
import com.bugu.queue.header.Header;
import com.bugu.queue.header.HeaderHelper;
import com.bugu.queue.header.HeaderState;
import com.bugu.queue.header.Pointer;
import com.bugu.queue.persistence.HeaderPersistence;
import com.bugu.queue.persistence.Persistence;
import com.bugu.queue.persistence.PersistenceRequest;
import com.bugu.queue.persistence.PersistenceResponse;
import com.bugu.queue.util.Logger;
import com.bugu.queue.util.RafHelper;
import com.bugu.queue.util.Size;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ImmutableFileQueue
 * <p>
 * {@link #capacity}
 * <p>
 * Author by xpl, Date on 2021/1/27.
 */
public class ImmutableFileQueue<E> implements FileQueue<E> {


    public static final long MIN_SIZE = Size._M << 1; // 2M
    public static final long THRESHOLD_SIZE = MIN_SIZE >> 3; // 128K

    private String path;
    private long capacity;
    private Converter.Factory factory = GsonConverterFactory.create();
    private Persistence mPersistence = new Persistence.PersistenceImpl();

    private Header fileQueueHeader;
    private RandomAccessFile writeRaf;
    private RandomAccessFile headerRaf;
    private RandomAccessFile readRaf;
    private Pointer headPoint = new AbsPointer.HeadPointer();
    private Pointer tailPoint = new AbsPointer.TailPointer();
    private Pointer lengthPoint = new AbsPointer.LengthPointer();
    private final HeaderPersistence mHeaderPersistence = new HeaderPersistence.HeaderPersistenceImpl();
    private AtomicInteger state = new AtomicInteger(STATE_CLOSE);
    private static final int STATE_CLOSE = 0;
    private static final int STATE_ON = 1;

    private OnFileQueueChanged onFileQueueChanged;
    private CheckDiskCallback checkDiskCallback;

    private ImmutableFileQueue() {
    }

    public ImmutableFileQueue(String path, Type type, Converter.Factory factory) {
        this(path, MIN_SIZE, type, factory);
    }

    public ImmutableFileQueue(String path, long capacity, Type type, Converter.Factory factory) {
        this.path = path;
        this.mType = type;
        this.capacity = Math.max(capacity, MIN_SIZE);
        this.factory = factory;
        initHeader();
    }

    private void initHeader() {
        RandomAccessFile r = null;
        try {
            File file = new File(path);
            File parentFile = file.getParentFile();

            boolean existFile = true;
            if (!parentFile.exists()) {
                existFile = false;
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    throw new FileQueueException("创建文件失败");
                }
            }
            if (!file.exists()) {
                existFile = false;
                boolean createNewFile = file.createNewFile();
                if (!createNewFile) {
                    throw new FileQueueException("创建文件失败");
                }
            }
            r = RafHelper.createRW(path);
            if (existFile) {
                parseHeader(r);

            } else {
                createHeader(r);
            }
            state.set(STATE_ON);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RafHelper.close(r);
        }
    }

    public void setFactory(Converter.Factory factory) {
        this.factory = factory;
    }

    private void createHeader(RandomAccessFile r) throws Exception {
        info("create header start ...");
        Header header = new Header();
        header.setVersion(Version.VERSION);
        long headerLength = Header.HEADER_LENGTH;
        header.setHead(headerLength);
        header.setTail(headerLength);
        header.setLength(capacity);
        r.setLength(capacity);
        this.mHeaderPersistence.write(header, r);
        this.fileQueueHeader = header;
    }

    private void parseHeader(RandomAccessFile r) throws Exception {
        info("parse header start ...");
        Header read = this.mHeaderPersistence.read(r);
        if (read != null) {
            HeaderState headerState = HeaderHelper.validateHeader(read);
            info("headerState : " + headerState);
            switch (headerState) {
                case INVALID:
                    throw new FileQueueException("不是FileQueue文件");
                case NOT_COMPLETE:
                case COMPLETE:
                case INIT:
                    this.fileQueueHeader = read;
                    break;
            }
        } else {
            throw new FileQueueException("不是FileQueue文件");
        }
    }

    @Override
    public Header getHeader() {
        return fileQueueHeader;
    }

    @Override
    public boolean delete() {
        // todo 正在添加/获取时删除？
        close();
        File file = new File(path);
        if (file.exists()) {
            boolean delete = file.delete();
            return delete;
        }
        return true;
    }

    private void checkState() throws FileQueueException {
        if (isClosed()) {
            throw new FileQueueException("fileQueue 已经关闭");
        }
    }

    @Override
    public void put(E e) throws Exception {
        checkState();
        if (e == null) throw new NullPointerException();
        final ReentrantLock putLock = this.putLock;
        //info("start put ...............");
        putLock.lockInterruptibly();
        if (isClosed()) {
            info("start put closed");
            putLock.unlock();
            return;
        }
        try {
            createWriteRandomAccessFile();
            this.writeRaf.setLength(fileQueueHeader.getLength());

            while (validateFull() || checkDiskFull()) {
                warning("< any more space to put ... >");
                notFull.await();
            }
            if (isClosed()) {
                putLock.unlock();
                return;
            }
            enqueue(e);
            notifyChanged(0);
            if (!(validateFull() || checkDiskFull())) {
                notFull.signal(); // 通知
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            putLock.unlock();
        }

        signalNotEmpty(); // 通知

    }

    private OnFileQueueStateChanged onFileQueueStateChanged;

    public void setOnFileQueueStateChanged(OnFileQueueStateChanged onFileQueueStateChanged) {
        this.onFileQueueStateChanged = onFileQueueStateChanged;
    }

    private boolean checkDiskFull() {
        boolean result = checkDiskCallback == null ? cDEFAULT.check(this) : checkDiskCallback.check(this);
        if (result) {
            if (onFileQueueStateChanged != null) {
                onFileQueueStateChanged.onChanged(this, State.FULL);
            }
        }
        return result;
    }

    boolean validateFull() {
        long tail = fileQueueHeader.getTail();
        long length = fileQueueHeader.getLength();
        boolean result = length - tail < THRESHOLD_SIZE;
        if (result) {
            info("<validateFull> tail = " + tail + " ,length = " + length + " ,result = " + result);
        }
        return result;
    }

    private void notifyChanged(int type) {
        String tag = type == 0 ? "put " : "take";
        // info(tag + " -> " + fileQueueHeader.toString());
        if (onFileQueueChanged != null) {
            onFileQueueChanged.onChanged(this, type, fileQueueHeader);
        }
    }

    private Type mType;

    public void setType(Type type) {
        this.mType = type;
    }

    private long enqueue(E e) throws Exception {
        long lastTail = fileQueueHeader.getTail();
        writeRaf.seek(lastTail);
        Converter<E, PersistenceRequest> converter = (Converter<E, PersistenceRequest>) factory.requestBodyConverter(mType, null, null, this);
        if (converter == null) throw new FileQueueException("convert is null");
        PersistenceRequest convert = converter.convert(e);
        mPersistence.write(convert, writeRaf);
        //transform.write(e, writeRaf);
        long currentTail = writeRaf.getFilePointer();
        this.fileQueueHeader.setTail(currentTail);
        createHeaderRandomAccessFile();
        tailPoint.write(headerRaf, currentTail);
        return currentTail;

    }

    private void createWriteRandomAccessFile() throws Exception {
        if (this.writeRaf == null) {
            this.writeRaf = RafHelper.createRW(path);

        }
    }

    private void createReadRandomAccessFile() throws IOException {
        if (this.readRaf == null) {
            this.readRaf = RafHelper.createRW(path);
        }
    }

    private void createHeaderRandomAccessFile() throws IOException {
        if (this.headerRaf == null) {
            this.headerRaf = RafHelper.createRW(path);
        }
    }

    @Override
    public E take() throws Exception {
        checkState();
        E e;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        if (isClosed()) {
            takeLock.unlock();
            throw new FileQueueException("  closed ");
        }
        try {
            createReadRandomAccessFile();
            this.readRaf.setLength(fileQueueHeader.getLength());
            while (validateEmpty()) {
                warning("< anymore data to take ... >");
                notEmpty.await();
            }
            if (isClosed()) {
                throw new FileQueueException("  closed ");
            }
            e = dequeue();
            if (e != null) {
                long currentHead = readRaf.getFilePointer();
                this.fileQueueHeader.setHead(currentHead);
                createHeaderRandomAccessFile();
                headPoint.write(headerRaf, currentHead);
                notifyChanged(1);
                if (!validateEmpty()) {
                    notEmpty.signal();
                }
                info("[take] ** = " + e.toString() + ",clz = " + e.getClass());
                return e;
            }
        } finally {
            takeLock.unlock();
        }
        //signalNotFull();
        warning("take fail");
        throw new FileQueueException("take fail");
    }

    @Override
    public boolean isClosed() {
        return state.get() == 0;
    }

    private boolean validateEmpty() {
        long head = fileQueueHeader.getHead();
        long tail = fileQueueHeader.getTail();
        return head >= tail;
    }

    private E dequeue() throws Exception {
        long lastHead = fileQueueHeader.getHead();
        readRaf.seek(lastHead);
        Converter<PersistenceResponse, E> converter = (Converter<PersistenceResponse, E>) factory.responseBodyConverter(mType, null, this);
        PersistenceResponse response = mPersistence.read(readRaf);
        E convert = converter.convert(response);
        return convert;
    }

    @Override
    public void close() {
        synchronized (this) {
            state.set(0);
            signalNotEmpty();
            signalNotFull();
            RafHelper.close(writeRaf);
            RafHelper.close(readRaf);
            RafHelper.close(headerRaf);
            fileQueueHeader = null;
            writeRaf = null;
            readRaf = null;
            headerRaf = null;
        }
    }

    public OnFileQueueChanged getOnFileQueueChanged() {
        return onFileQueueChanged;
    }

    public void setOnFileQueueChanged(OnFileQueueChanged onFileQueueChanged) {
        this.onFileQueueChanged = onFileQueueChanged;
    }

    public void setCheckDiskCallback(CheckDiskCallback checkDiskCallback) {
        this.checkDiskCallback = checkDiskCallback;
    }

    RandomAccessFile getWriteRaf() {
        return writeRaf;
    }

    public RandomAccessFile getHeaderRaf() throws IOException {
        createHeaderRandomAccessFile();
        return headerRaf;
    }

    Pointer getTailPoint() {
        return tailPoint;
    }

    Pointer getLengthPoint() {
        return lengthPoint;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Lock held by take, poll, etc
     */
    private final ReentrantLock takeLock = new ReentrantLock();

    /**
     * Wait queue for waiting takes
     */
    private final Condition notEmpty = takeLock.newCondition();

    /**
     * Lock held by put, offer, etc
     */
    private final ReentrantLock putLock = new ReentrantLock();

    /**
     * Wait queue for waiting puts
     */
    private final Condition notFull = putLock.newCondition();

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * Signals a waiting put. Called only from take/poll.
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    private void info(String msg) {
        Logger.info(msg);
    }

    private void warning(String msg) {
        Logger.warning(msg);
    }

    public static interface CheckDiskCallback {
        boolean check(FileQueue<?> fileQueue);
    }

    private final CheckDiskCallback cDEFAULT = (fileQueue) -> true;
}
