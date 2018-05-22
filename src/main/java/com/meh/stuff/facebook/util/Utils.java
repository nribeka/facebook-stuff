package com.meh.stuff.facebook.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class.getSimpleName());

    private Utils() {
        // private constructor for utility class
    }

    // This class implementation is copied (if not inspired by) the IO source code.
    public static void copyFile(File input, File output) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        long bufferSize = 1024 * 1024 * 30;

        try {
            fileInputStream = new FileInputStream(input);
            fileOutputStream = new FileOutputStream(output);

            inputChannel = fileInputStream.getChannel();
            outputChannel = fileOutputStream.getChannel();

            long size = inputChannel.size();

            long count = 0;
            long position = 0;

            while (position < size) {
                long remain = size - position;
                count = remain > bufferSize ? bufferSize : remain;
                long bytesCopied = outputChannel.transferFrom(inputChannel, position, count);
                if (bytesCopied == 0) {
                    break;
                }

                position = position + bytesCopied;
            }
        } catch (IOException e) {
            log.error("Unable to copy file from {} to {}.", input.getAbsolutePath(), output.getAbsolutePath(), e);
        } finally {
            closeQuietly(inputChannel);
            closeQuietly(outputChannel);
            closeQuietly(fileInputStream);
            closeQuietly(fileOutputStream);
        }
    }

    public static void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            // just silently ignore the exception.
        }
    }

    private static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // do nothing since we're quietly closing it.
        }
    }
}
