package com.bugu.queue.transform;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/28.
 */
public abstract class AbsTransform<E> implements Transform<E> {
    private final static int BUFFER_LENGTH = 1024 * 3;

    protected void write0(RandomAccessFile raf, byte[] data) throws Exception {
        if (data == null) {
            return;
        }
        int length = data.length;
        System.out.println("[write] index = " + length + "/" + BUFFER_LENGTH + " = " + length / BUFFER_LENGTH);
        // 1.先写收据长度
        raf.writeLong(length);
        // 2.再写数据
        int start = 0;
        int len;
        while (true) {
            int left = length - start;
            if (left <= 0) {
                break;
            } else {
                len = Math.min(left, BUFFER_LENGTH);
            }
            System.out.println("[write] length = " + length + " ,start = " + start + " ,writeLen = " + len + " ,left = " + left);
            raf.write(data, start, len);
            start += len;
        }
    }

    protected byte[] read0(RandomAccessFile raf) throws Exception {
        // 1.先读数据长度
        long length = raf.readLong();
        if (length < 0) throw new IllegalStateException("无法读取数据长度");
        System.out.println("[read] length = " + length);
        // 2.读数据
        byte[] buffer = new byte[BUFFER_LENGTH];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int sum = 0;
        while (true) {
            if (sum > length) {
                throw new IllegalStateException("超过了length，读文件可能出错了");
            } else if (sum == length) {
                break;
            } else {
                // 读之前要判断一下，确定读多少。
                int read;
                if (sum + BUFFER_LENGTH <= length) {
                    // 读满
                    read = raf.read(buffer);
                } else {
                    // 说明读多了
                    long offset = sum + BUFFER_LENGTH - length;
                    int realRead = BUFFER_LENGTH - (int) offset;
                    read = raf.read(buffer, 0, realRead);
                }
                out.write(buffer, 0, read);
                sum += read;
            }
        }
        byte[] bytes = out.toByteArray();
        System.out.println("[read] end = " + bytes.length);
        out.flush();
        out.close();
        return bytes;
    }
}
