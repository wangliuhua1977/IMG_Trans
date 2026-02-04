package com.wangliuhua.imgtrans.model;

import java.io.File;

public class ImageItem {
    private final File file;
    private final String format;
    private final long sizeBytes;
    private final int width;
    private final int height;

    public ImageItem(File file, String format, long sizeBytes, int width, int height) {
        this.file = file;
        this.format = format;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
    }

    public File getFile() {
        return file;
    }

    public String getFormat() {
        return format;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
