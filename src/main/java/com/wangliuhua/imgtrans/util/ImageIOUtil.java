package com.wangliuhua.imgtrans.util;

import javax.imageio.ImageIO;

public final class ImageIOUtil {
    private ImageIOUtil() {
    }

    public static void registerImageIO() {
        ImageIO.scanForPlugins();
    }
}
