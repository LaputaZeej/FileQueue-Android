package com.bugu.queue.util;

import com.bugu.queue.FileQueue;
import com.bugu.queue.header.AbsPointer;
import com.bugu.queue.header.Header;
import com.bugu.queue.header.Pointer;
import com.bugu.queue.transform.GsonTransform;
import com.bugu.queue.transform.ProtobufTransform;
import com.bugu.queue.transform.Transform;
import com.google.protobuf.MessageLite;

import java.io.RandomAccessFile;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Author by xpl, Date on 2021/1/27.
 */
public class FileQueueCompat {


    @Target(ElementType.PARAMETER)
    @Retention(SOURCE)
    @IntDef({Type.GSON, Type.PROTOBUF})
    public @interface Type {
        int GSON = 0;
        int PROTOBUF = 1;
    }

    @Retention(SOURCE)
    @Target({ANNOTATION_TYPE})
    private @interface IntDef {
        /**
         * Defines the allowed constants for this element
         */
        int[] value() default {};

        /**
         * Defines whether the constants can be used as a flag, or just as an enum (the default)
         */
        boolean flag() default false;

        /**
         * Whether any other values are allowed. Normally this is
         * not the case, but this allows you to specify a set of
         * expected constants, which helps code completion in the IDE
         * and documentation generation and so on, but without
         * flagging compilation warnings if other values are specified.
         */
        boolean open() default false;
    }

    public static void compressAlreadyRead(FileQueue<?> queue) {
        Header header = queue.getHeader();
        long headPoint = header.getHead();
        long tailPoint = header.getTail();
        long offset = tailPoint - headPoint;
        Logger.info("headPoint = " + headPoint + ",tailPoint = " + tailPoint + ",offset= " + offset);
        if (headPoint > Header.HEADER_LENGTH && offset > 0) {
            long newHeadPoint = Header.HEADER_LENGTH;
            long newTailPoint = Header.HEADER_LENGTH + offset;
            Logger.info("newHeadPoint = " + newHeadPoint + ",newTailPoint = " + newTailPoint);
            RandomAccessFile writeRaf = null;
            try {
                writeRaf = RafHelper.createRW(queue.getPath());
                byte[] temp = new byte[1024];
                int count;
                long newHeadPointer = 0;
                long oldHeadPointer = headPoint;
                boolean writeHeaded = false;
                Pointer headPointer = new AbsPointer.HeadPointer();
                Pointer tailPointer = new AbsPointer.TailPointer();
                Pointer lengthPointer = new AbsPointer.LengthPointer();
                while (true) {
                    if (!writeHeaded) {
                        writeRaf.seek(0);
                        writeRaf.writeLong(newHeadPoint);
                        writeRaf.writeLong(newTailPoint);
                        newHeadPointer = writeRaf.getFilePointer();
                        writeRaf.seek(oldHeadPointer);
                        writeHeaded = true;
                    }

                    count = writeRaf.read(temp);
                    Logger.info("count:" + count);
                    if (count == -1) {
                        break;
                    }

                    //Logger.logger("temp :" + new String(temp));

                    writeRaf.seek(newHeadPointer);
                    writeRaf.write(temp, 0, count);
                    oldHeadPointer += count;
                    newHeadPointer += count;
                    writeRaf.seek(oldHeadPointer);
                }
                //newLength += FileQueue.DEFAULT_LENGTH;
                writeRaf.setLength(newTailPoint);
                headPointer.write(writeRaf, newHeadPoint);
                tailPointer.write(writeRaf, newTailPoint);
                lengthPointer.write(writeRaf, newTailPoint);
                Logger.info("clear end.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static <E> Transform<E> getTransform(Class<E> clz, @Type int type) {
        switch (type) {
            case Type.GSON:
                return new GsonTransform<E>(clz);
            case Type.PROTOBUF:
                if (MessageLite.class.isAssignableFrom(clz)) {
                    ProtobufTransform<?> transform = new ProtobufTransform(clz);
                    return (Transform<E>) transform;
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}
