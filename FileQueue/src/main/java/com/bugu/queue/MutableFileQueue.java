package com.bugu.queue;

import com.bugu.queue.header.Header;
import com.bugu.queue.transform.Transform;
import com.bugu.queue.util.Logger;
import com.bugu.queue.util.Size;

import java.io.RandomAccessFile;

import static com.bugu.queue.ImmutableFileQueue.MIN_SIZE;
import static com.bugu.queue.ImmutableFileQueue.THRESHOLD_SIZE;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class MutableFileQueue<E> implements FileQueue<E> {
    private ImmutableFileQueue<E> fileQueue;
    private OnFileQueueChanged onFileQueueChanged;
    private OnFileQueueStateChanged onFileQueueStateChanged;
    private long max;
    private static final long MAX_SIZE_DEFAULT = Size._G;

    public MutableFileQueue(ImmutableFileQueue<E> fileQueue, long max) {
        this.max = max <= 0 ? MAX_SIZE_DEFAULT : max;
        initFileQueue(fileQueue);
    }

    private MutableFileQueue(String path, long capacity, long max, Transform<E> transform) {
        this(new ImmutableFileQueue<E>(path, capacity, transform), max);
    }

    private void initFileQueue(ImmutableFileQueue<E> fileQueue) {
        this.fileQueue = fileQueue;
        this.fileQueue.setCheckDiskCallback((fq) -> {
            long length = fq.getHeader().getLength();
            long max = MutableFileQueue.this.max;
            if (length >= max) {
                Logger.info("<CheckDiskCallback> length = " + length + " ,max = " + max);
                if (onFileQueueStateChanged != null) {
                    onFileQueueStateChanged.onChanged(this, State.FULL);
                }
                return true;
            } else {
                return false;
            }
        });
        this.fileQueue.setOnFileQueueChanged((fq, type, header) -> {
            if (type == 0) {
                tryCapacity(header);
            }
            if (onFileQueueChanged != null) {
                onFileQueueChanged.onChanged(fq, type, header);
            }
        });

    }

    private void tryCapacity(Header fileQueueHeader) {
        RandomAccessFile writeRaf = null;
        try {
            long tail = fileQueueHeader.getTail();
            long length = fileQueueHeader.getLength();
            if (Math.abs(length - tail) <= THRESHOLD_SIZE) {
                length = length + MIN_SIZE;
                if (length > max) {
                    Logger.info("tryCapacity -> max size");
                    if (onFileQueueStateChanged != null) {
                        onFileQueueStateChanged.onChanged(this, State.FULL);
                    }
                    return;
                }
                Logger.info("capacity!!! length = " + length);
                fileQueueHeader.setLength(length);
                writeRaf = fileQueue.getHeaderRaf();
                fileQueue.getHeader().setLength(length);
                fileQueue.getLengthPoint().write(writeRaf, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getMax() {
        return max;
    }

    @Override
    public void close() {
        fileQueue.close();
    }

    @Override
    public void put(E e) throws Exception {
        fileQueue.put(e);
    }

    @Override
    public E take() throws Exception {
        return fileQueue.take();
    }

    @Override
    public Header getHeader() {
        return fileQueue.getHeader();
    }

    @Override
    public String getPath() {
        return fileQueue.getPath();
    }

    @Override
    public boolean delete() {
        return fileQueue.delete();
    }

    @Override
    public boolean isClosed() {
        return fileQueue.isClosed();
    }

    public void setOnFileQueueChanged(OnFileQueueChanged onFileQueueChanged) {
        this.onFileQueueChanged = onFileQueueChanged;
    }

    public void setOnFileQueueStateChanged(OnFileQueueStateChanged onFileQueueStateChanged) {
        this.onFileQueueStateChanged = onFileQueueStateChanged;
        this.fileQueue.setOnFileQueueStateChanged(onFileQueueStateChanged);
    }

    public static class Builder<E> {
        private String path;
        private long maxSize;
        private long capacity;
        private boolean debug;
        private Transform<E> transform;

        public Builder() {
        }

        public Builder<E> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<E> capacity(long capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder<E> maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder<E> debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder<E> transform(Transform<E> transform) {
            this.transform = transform;
            return this;
        }

        public MutableFileQueue<E> build() {
            MutableFileQueue<E> fileQueue = new MutableFileQueue<>(path, capacity, maxSize, transform);
            return fileQueue;
        }
    }
}
