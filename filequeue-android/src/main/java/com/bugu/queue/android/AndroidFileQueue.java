package com.bugu.queue.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.format.Formatter;

import androidx.core.content.ContextCompat;

import com.bugu.queue.FileQueue;
import com.bugu.queue.MutableFileQueue;
import com.bugu.queue.OnFileQueueChanged;
import com.bugu.queue.header.Header;
import com.bugu.queue.transform.Transform;
import com.bugu.queue.util.FileQueueCompat;
import com.bugu.queue.util.Size;

import java.util.Locale;

/**
 * Author by xpl, Date on 2021/1/25.
 */
public class AndroidFileQueue<E> {
    private Context context;
    private MutableFileQueue<E> fileQueue;
    private String path;
    private long maxSize = 10 * Size._G;
    private OnFileChanged onFileChanged;

    public AndroidFileQueue(final Context context, String path, Transform<E> transform) {
        this.context = context;
        this.path = path;
        checkPermission(context);
        initFileQueue(path, transform);
    }

  /*  public AndroidFileQueue(final Context context, String path, Class<E> clz, int type) {
        this.context = context;
        this.path = path;
        checkPermission(context);
        Transform<E> transform = getTransform(clz, type);
        initFileQueue(path, transform);

    }

    public AndroidFileQueue(final Context context, String path, Class<E> clz) {
        this(context, path, clz, FileQueueCompat.Type.GSON);
    }*/

    private void initFileQueue(String path, Transform<E> transform) {
        if (transform == null) throw new IllegalStateException("没有Transform");
        this.fileQueue = new MutableFileQueue<E>(path, maxSize, transform);
        this.fileQueue.setOnFileQueueChanged(new OnFileQueueChanged() {
            @Override
            public void onChanged(FileQueue<?> fileQueue, int i, Header fileQueueHeader) {
                long head = fileQueueHeader.getHead();
                long tail = fileQueueHeader.getTail();
                long length = fileQueueHeader.getLength();
                String formatLength = Formatter.formatFileSize(AndroidFileQueue.this.context, fileQueueHeader.getLength());
                String formatMaxSize = Formatter.formatFileSize(AndroidFileQueue.this.context, maxSize);
                String ratio = String.format(Locale.getDefault(), "%d%%", length * 100 / maxSize);
                String logger = "[head = " + head + " tail = " + tail + "]" + formatLength + "/" + formatMaxSize + "[" + ratio + "]";
                System.out.println(logger);
                if (onFileChanged != null) {
                    onFileChanged.onChanged(AndroidFileQueue.this, logger);
                }
            }
        });
    }

    private Transform<E> getTransform(Class<E> clz, int type) {
        return FileQueueCompat.getTransform(clz, type);
    }

    private void checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("请申请权限后再使用，权限名称：" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public OnFileChanged getOnFileChanged() {
        return onFileChanged;
    }

    public void setOnFileChanged(OnFileChanged onFileChanged) {
        this.onFileChanged = onFileChanged;
    }

    public void close() {
        fileQueue.close();
    }

    public void put(E e) throws Exception {
        checkPermission(context);
        fileQueue.put(e);
    }

    public E take() throws Exception {
        checkPermission(context);
        return fileQueue.take();
    }

    public boolean delete() {
        checkPermission(context);
        return fileQueue.delete();
    }

    public boolean isClosed(){
        return fileQueue.isClosed();
    }

    Header getHeader() {
        checkPermission(context);
        return fileQueue.getHeader();
    }
}
