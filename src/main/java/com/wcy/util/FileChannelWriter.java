package com.wcy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelWriter {
    private FileOutputStream fileOut;
    private FileChannel fileChannel;
    private ByteBuffer byteBuffer;

    public FileChannelWriter(String fileName) throws FileNotFoundException {
        File file=new File(fileName);
        fileOut=new FileOutputStream(file, true);
        fileChannel=fileOut.getChannel();
    }

    public long write(byte[] array) throws IOException {
        /**
         * after wrap(), the position of ByteBuffer will be zero
         */
        this.byteBuffer=ByteBuffer.wrap(array);
        long writeSize=fileChannel.write(this.byteBuffer);

        this.byteBuffer.clear();
        fileOut.flush();

        return writeSize;
    }

    public void close() throws IOException {
        fileChannel.close();
        fileOut.close();
    }
}
