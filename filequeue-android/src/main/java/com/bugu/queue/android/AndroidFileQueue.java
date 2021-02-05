package com.bugu.queue.android;

import android.content.Context;
import android.text.format.Formatter;

import com.bugu.queue.FileQueue;
import com.bugu.queue.MutableFileQueue;
import com.bugu.queue.header.Header;
import com.bugu.queue.transform.Transform;

import java.util.Locale;

/**
 * Author by xpl, Date on 2021/1/25.
 */
public class AndroidFileQueue<E> implements FileQueue<E> {
    private Context context;
    private MutableFileQueue<E> mFileQueue;
    private String mPath;
    private long mMaxSize;
    private long capacity;
    private OnFileChanged mOnFileChanged;

    private AndroidFileQueue(final Context context, String path, long capacity, long maxSize, Transform<E> transform) {
        if (transform == null) throw new IllegalStateException("没有Transform");
        this.context = context;
        this.mPath = path;
        this.capacity = capacity;
        this.mMaxSize = maxSize;
        checkPermission(context);

        this.mFileQueue = new MutableFileQueue.Builder<E>()
                .path(path)
                .maxSize(mMaxSize)
                .capacity(capacity)
                .transform(transform)
                .build();
        this.mFileQueue.setOnFileQueueChanged((fileQueue, type, header) -> {
            long head = header.getHead();
            long tail = header.getTail();
            long length = header.getLength();
            String formatLength = Formatter.formatFileSize(AndroidFileQueue.this.context, header.getLength());
            String formatMaxSize = Formatter.formatFileSize(AndroidFileQueue.this.context, mMaxSize);
            String ratio = String.format(Locale.getDefault(), "%d%%", length * 100 / mMaxSize);
            String logger = "[head = " + head + " tail = " + tail + "]" + formatLength + "/" + formatMaxSize + "[" + ratio + "]";
            System.out.println(logger);
            if (mOnFileChanged != null) {
                mOnFileChanged.onChanged(AndroidFileQueue.this, logger);
            }
        });
        this.mFileQueue.setOnFileQueueStateChanged((fileQueue, state) -> {
            if (mOnFileChanged != null) {
                mOnFileChanged.onStateChanged(AndroidFileQueue.this, state == FileQueue.State.FULL);
            }
        });
    }

    private void checkPermission(Context context) {
        /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("请申请权限后再使用，权限名称：" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
    }

    public void setOnFileChanged(OnFileChanged mOnFileChanged) {
        this.mOnFileChanged = mOnFileChanged;
    }

    @Override
    public void close() {
        mFileQueue.close();
    }

    @Override
    public void put(E e) throws Exception {
        checkPermission(context);
        mFileQueue.put(e);
    }

    @Override
    public E take() throws Exception {
        checkPermission(context);
        return mFileQueue.take();
    }

    @Override
    public boolean delete() {
        checkPermission(context);
        return mFileQueue.delete();
    }

    @Override
    public boolean isClosed() {
        return mFileQueue.isClosed();
    }

    @Override
    public Header getHeader() {
        checkPermission(context);
        return mFileQueue.getHeader();
    }

    @Override
    public String getPath() {
        return mFileQueue.getPath();
    }

    public static class Builder<E> {
        private Context context;
        private String path;
        private long maxSize;
        private long capacity;
        private boolean debug;
        private Transform<E> transform;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder<E> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<E> maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder<E> capacity(long capacity) {
            this.capacity = capacity;
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

        public AndroidFileQueue<E> build() {
            AndroidFileQueue<E> eAndroidFileQueue = new AndroidFileQueue<E>(this.context, this.path, capacity, this.maxSize, transform);
            return eAndroidFileQueue;
        }
    }

}
