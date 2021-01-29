package com.bugu.queue;

import com.bugu.queue.bean.FileQueueException;
import com.bugu.queue.header.AbsPointer;
import com.bugu.queue.header.Header;
import com.bugu.queue.header.HeaderHelper;
import com.bugu.queue.header.HeaderState;
import com.bugu.queue.header.Pointer;
import com.bugu.queue.transform.HeaderTransform;
import com.bugu.queue.transform.Transform;
import com.bugu.queue.util.Logger;
import com.bugu.queue.util.RafHelper;
import com.bugu.queue.util.Size;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class ImmutableFileQueue<E> implements FileQueue<E> {


    public static final long MIN_SIZE = Size._M * 2;

    private String path;
    private long capacity;
    private Transform<E> transform;
    private Header fileQueueHeader;
    private RandomAccessFile writeRaf;
    private RandomAccessFile headerRaf;
    private RandomAccessFile readRaf;
    private Pointer headPoint = new AbsPointer.HeadPointer();
    private Pointer tailPoint = new AbsPointer.TailPointer();
    private Pointer lengthPoint = new AbsPointer.LengthPointer();
    private final HeaderTransform headerTransform = new HeaderTransform();
    private AtomicInteger state = new AtomicInteger(STATE_CLOSE);
    private static final int STATE_CLOSE = 0;
    private static final int STATE_ON = 1;

    private OnFileQueueChanged onFileQueueChanged;
    private CheckDiskCallback checkDiskCallback;

    private ImmutableFileQueue() {
    }

    public ImmutableFileQueue(String path, Transform<E> transform) {
        this(path, MIN_SIZE, transform);
    }

    public ImmutableFileQueue(String path, long capacity, Transform<E> transform) {
        this.path = path;
        this.capacity = Math.max(capacity, MIN_SIZE);
        this.transform = transform;
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

    private void createHeader(RandomAccessFile r) throws Exception {
        info("create header start ...");
        Header header = new Header();
        header.setVersion(Version.VERSION);
        long headerLength = Header.HEADER_LENGTH;
        header.setHead(headerLength);
        header.setTail(headerLength);
        header.setLength(capacity);
        r.setLength(capacity);
        this.headerTransform.write(header, r);
        this.fileQueueHeader = header;
    }

    private void parseHeader(RandomAccessFile r) throws Exception {
        info("parse header start ...");
        Header read = this.headerTransform.read(r);
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
        } finally {
            putLock.unlock();
        }

        signalNotEmpty(); // 通知

    }

    private boolean checkDiskFull() {
        return checkDiskCallback == null ? cDEFAULT.check(this) : checkDiskCallback.check(this);
    }

    private boolean validateFull() {
        long tail = fileQueueHeader.getTail();
        long length = fileQueueHeader.getLength();
        return length - tail <= MIN_SIZE / 8;
    }

    private void notifyChanged(int type) {
        String tag = type == 0 ? "put " : "take";
        // info(tag + " -> " + fileQueueHeader.toString());
        if (onFileQueueChanged != null) {
            onFileQueueChanged.onChanged(this, type, fileQueueHeader);
        }
    }

    private long enqueue(E e) throws Exception {
        long lastTail = fileQueueHeader.getTail();
        writeRaf.seek(lastTail);
        transform.write(e, writeRaf);
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
            }
        } finally {
            takeLock.unlock();
        }
        //signalNotFull();
        return e;
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
        return transform.read(readRaf);
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
