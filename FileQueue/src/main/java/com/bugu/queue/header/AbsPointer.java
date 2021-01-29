package com.bugu.queue.header;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public abstract class AbsPointer implements Pointer {
    @Override
    public void write(RandomAccessFile raf, long value) throws IOException {
        raf.seek(point());
        raf.writeLong(value);
    }

    @Override
    public long read(RandomAccessFile raf) throws IOException {
        raf.seek(point());
        return raf.readLong();
    }

    public static class ExtraPointer extends AbsPointer{
        @Override
        public long point() {
            return 32;
        }
    }

    public static class HeadPointer  extends AbsPointer{
        @Override
        public long point() {
            return 8;
        }
    }

    public static class LengthPointer  extends AbsPointer{
        @Override
        public long point() {
            return 24;
        }
    }

    public static class TailPointer  extends AbsPointer{
        @Override
        public long point() {
            return 16;
        }
    }

}
