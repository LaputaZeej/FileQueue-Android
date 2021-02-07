package com.bugu.queue.persistence;

import com.bugu.queue.header.Header;
import com.bugu.queue.util.Logger;

import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/2/7.
 */
public interface HeaderPersistence {
    boolean DEBUG = false;

    void write(Header header, RandomAccessFile raf) throws Exception;

    Header read(RandomAccessFile raf) throws Exception;

    class HeaderPersistenceImpl implements HeaderPersistence {


        @Override
        public void write(Header header, RandomAccessFile raf) throws Exception {
            try {
                Logger.info("write Head : " + header.toString());
                raf.writeLong(header.getVersion());
                raf.writeLong(header.getHead());
                raf.writeLong(header.getTail());
                raf.writeLong(header.getLength());
                raf.writeLong(header.getExtra());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Header read(RandomAccessFile raf) throws Exception {
            try {
                Header header = new Header();
                header.setVersion(raf.readLong());
                header.setHead(raf.readLong());
                header.setTail(raf.readLong());
                header.setLength(raf.readLong());
                header.setExtra(raf.readLong());
                Logger.info("readHead : " + header.toString());
                return header;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
