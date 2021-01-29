package com.bugu.queue.transform;

import com.bugu.queue.header.Header;
import com.bugu.queue.util.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class HeaderTransform implements Transform<Header>{
    @Override
    public void write(Header fileQueueHeader, RandomAccessFile raf) throws IOException {
        try {
            Logger.info("write Head : " + fileQueueHeader.toString());
            raf.writeLong(fileQueueHeader.getVersion());
            raf.writeLong(fileQueueHeader.getHead());
            raf.writeLong(fileQueueHeader.getTail());
            raf.writeLong(fileQueueHeader.getLength());
            raf.writeLong(fileQueueHeader.getExtra());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Header read(RandomAccessFile raf) throws IOException {
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
