package com.bugu.things.storage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public abstract class AbsTransform {
    private final static int MAX_LENGTH = 65535;
    private final static int BUFFER_LENGTH = 4096;
    private static final String SEPARATOR = ";";

    public static void write(RandomAccessFile raf, byte[] data) throws Exception {
        if (data == null) {
            return;
        }
        int start = 0;
        int length = data.length;
        System.out.println("write index = " + length + "/" + BUFFER_LENGTH + " = " + length / BUFFER_LENGTH);
        // 1.先写收据长度
        raf.writeInt(length);
        // 2.再写数据
        int len;
        int index = 0;
        while (true) {
            int left = length - start;
            if (left <= 0) {
                break;
            } else {
                len = Math.min(left, BUFFER_LENGTH);
            }
            System.out.println("write length = " + length + " start = " + start + " writeLen = " + len + " index = " + index + " left = " + left);
            raf.write(data, start, len);
            start += len;
            index++;
        }
    }


    public static byte[] read(RandomAccessFile raf) throws Exception {
        // 1.先读数据长度
        int length = raf.readInt();
        if (length == -1) {
            throw new IllegalStateException("无法读取数据长度");
        }
        System.out.println("[read] length = " + length);
        // 2.读数据
        byte[] buffer = new byte[BUFFER_LENGTH];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int sum = 0;
        while (true) {
            if (sum > length) {
                throw new IllegalStateException("计算有问题");
            } else if (sum == length) {
                break;
            } else {
                int read = raf.read(buffer);
                System.out.println("[read] index = " + sum + " ,read = " + read);
                int temp = sum + read;
                if (temp - length <= 0) {
                    out.write(buffer, 0, read);
                    sum = temp;
                } else {
                    // 说明读多了
                    int offset = temp - length;
                    int realRead = BUFFER_LENGTH - offset;
                    out.write(buffer, 0, realRead);
                    sum += realRead;
                }

                if (read == -1) {
                    throw new IllegalStateException("EOF 文件结束了?");
                }
            }
        }
        byte[] bytes = out.toByteArray();
        System.out.println("[read] end = " + bytes.length);
        return bytes;
    }


    private static boolean isSeparator(byte[] data) {
        if (data == null || data.length == 0) return false;
        byte[] bytes = SEPARATOR.getBytes();
        if (data.length < bytes.length) return false;

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != data[i]) {
                System.out.println("[read] " + " ," + bytes[i] + " vs " + data[i]);
                return false;
            }
        }
        return true;
    }

}
