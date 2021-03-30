package com.bugu.queue.persistence;

import com.bugu.queue.ImmutableFileQueue;
import com.bugu.queue.bean.FileQueueException;
import com.bugu.queue.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public interface Persistence {
    boolean DEBUG = false;

    void write(PersistenceRequest request, RandomAccessFile raf) throws Exception;

    PersistenceResponse read(RandomAccessFile raf) throws Exception;

    class PersistenceImpl implements Persistence {
        private final static int BUFFER_LENGTH = 1024 * 3;

        @Override
        public void write(PersistenceRequest request, RandomAccessFile raf) throws Exception {
            write0(raf, request.getData());
        }

        @Override
        public PersistenceResponse read(RandomAccessFile raf) throws Exception {
            byte[] bytes = read0(raf);
            PersistenceResponse response = new PersistenceResponse(bytes);
            return response;
        }

        protected void write0(RandomAccessFile raf, byte[] data) throws Exception {
            if (data == null) {
                return;
            }
            checkSize(data);
            int length = data.length;
            log("[write] index = " + length + "/" + BUFFER_LENGTH + " = " + length / BUFFER_LENGTH);
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
                log("[write] length = " + length + " ,start = " + start + " ,writeLen = " + len + " ,left = " + left);
                raf.write(data, start, len);
                start += len;
            }
        }

        private void checkSize(byte[] data) throws FileQueueException {
            if (data.length > ImmutableFileQueue.THRESHOLD_SIZE) {
                throw new FileQueueException("数据过大");
            }
        }

        protected byte[] read0(RandomAccessFile raf) throws Exception {
            // 1.
            long length = raf.readLong();
            if (length < 0) throw new IllegalStateException("无法读取数据长度");
            log("[read] length = " + length);
            // 2.
            byte[] buffer = new byte[BUFFER_LENGTH];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int sum = 0;
            while (true) {
                if (sum > length) {
                    throw new IllegalStateException("sum > length error");
                } else if (sum == length) {
                    break;
                } else {
                    int read;
                    if (sum + BUFFER_LENGTH <= length) {
                        read = raf.read(buffer);
                    } else {
                        long offset = sum + BUFFER_LENGTH - length;
                        int realRead = BUFFER_LENGTH - (int) offset;
                        read = raf.read(buffer, 0, realRead);
                    }
                    out.write(buffer, 0, read);
                    sum += read;
                }
            }
            byte[] bytes = out.toByteArray();
            log("[read] end = " + bytes.length);
            out.flush();
            out.close();
            return bytes;
        }

        private void log(String msg) {
            if (!DEBUG) return;
            Logger.info(msg);
        }
    }


}
