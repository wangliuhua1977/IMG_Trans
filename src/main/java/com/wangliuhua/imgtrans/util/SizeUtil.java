package com.wangliuhua.imgtrans.util;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SizeUtil {
    private SizeUtil() {
    }

    public static Set<Integer> parseCustomSizes(String text) throws IllegalArgumentException {
        Set<Integer> sizes = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return sizes;
        }
        String[] parts = text.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int value;
            try {
                value = Integer.parseInt(trimmed);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("自定义尺寸包含非数字: " + trimmed, ex);
            }
            if (value < 1 || value > 1024) {
                throw new IllegalArgumentException("尺寸超出范围(1~1024): " + value);
            }
            sizes.add(value);
        }
        return sizes;
    }
}
