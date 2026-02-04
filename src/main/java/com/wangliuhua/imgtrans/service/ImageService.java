package com.wangliuhua.imgtrans.service;

import com.wangliuhua.imgtrans.model.ScaleMode;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageService {
    private ImageService() {
    }

    public static BufferedImage read(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("无法读取图片: " + file.getName());
        }
        return image;
    }

    public static BufferedImage scale(BufferedImage source, int size, ScaleMode mode, Color backgroundColor) {
        int targetWidth = size;
        int targetHeight = size;
        BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = target.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        if (backgroundColor != null) {
            g2d.setComposite(AlphaComposite.Src);
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, targetWidth, targetHeight);
        }

        int srcW = source.getWidth();
        int srcH = source.getHeight();
        if (mode == ScaleMode.STRETCH) {
            g2d.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } else if (mode == ScaleMode.FIT_CROP) {
            double scale = Math.max(targetWidth / (double) srcW, targetHeight / (double) srcH);
            int drawW = (int) Math.round(srcW * scale);
            int drawH = (int) Math.round(srcH * scale);
            int x = (targetWidth - drawW) / 2;
            int y = (targetHeight - drawH) / 2;
            g2d.drawImage(source, x, y, drawW, drawH, null);
        } else {
            double scale = Math.min(targetWidth / (double) srcW, targetHeight / (double) srcH);
            int drawW = (int) Math.round(srcW * scale);
            int drawH = (int) Math.round(srcH * scale);
            int x = (targetWidth - drawW) / 2;
            int y = (targetHeight - drawH) / 2;
            g2d.drawImage(source, x, y, drawW, drawH, null);
        }
        g2d.dispose();
        return target;
    }

    public static boolean hasAlpha(BufferedImage image) {
        return image.getColorModel().hasAlpha();
    }
}
