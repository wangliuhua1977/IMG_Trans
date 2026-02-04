package com.wangliuhua.imgtrans.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class IcoWriter {
    private IcoWriter() {
    }

    public static void write(List<BufferedImage> images, File output) throws IOException {
        if (images == null || images.isEmpty()) {
            throw new IOException("ICO 输出图片为空");
        }
        List<byte[]> pngData = new ArrayList<>();
        for (BufferedImage image : images) {
            pngData.add(toPng(image));
        }
        int count = pngData.size();
        int headerSize = 6 + count * 16;
        int offset = headerSize;
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(output))) {
            writeLEShort(out, 0);
            writeLEShort(out, 1);
            writeLEShort(out, count);
            for (int i = 0; i < count; i++) {
                BufferedImage image = images.get(i);
                byte width = (byte) (image.getWidth() >= 256 ? 0 : image.getWidth());
                byte height = (byte) (image.getHeight() >= 256 ? 0 : image.getHeight());
                out.write(width);
                out.write(height);
                out.write(0);
                out.write(0);
                writeLEShort(out, 1);
                writeLEShort(out, 32);
                byte[] data = pngData.get(i);
                writeLEInt(out, data.length);
                writeLEInt(out, offset);
                offset += data.length;
            }
            for (byte[] data : pngData) {
                out.write(data);
            }
        }
    }

    private static byte[] toPng(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean written = ImageIO.write(image, "png", baos);
            if (!written) {
                throw new IOException("PNG 编码失败");
            }
            return baos.toByteArray();
        }
    }

    private static void writeLEShort(BufferedOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    private static void writeLEInt(BufferedOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }
}
