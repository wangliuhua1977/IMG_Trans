package com.wangliuhua.imgtrans.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FileUtil {
    private static final String[] SUPPORTED_EXT = {"png", "jpg", "jpeg", "bmp", "gif", "tif", "tiff", "webp"};

    private FileUtil() {
    }

    public static List<File> listImages(File file, boolean recursive) {
        List<File> results = new ArrayList<>();
        if (file == null || !file.exists()) {
            return results;
        }
        if (file.isFile()) {
            if (isSupported(file)) {
                results.add(file);
            }
            return results;
        }
        File[] children = file.listFiles();
        if (children == null) {
            return results;
        }
        for (File child : children) {
            if (child.isDirectory() && recursive) {
                results.addAll(listImages(child, true));
            } else if (child.isFile() && isSupported(child)) {
                results.add(child);
            }
        }
        return results;
    }

    public static boolean isSupported(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        for (String ext : SUPPORTED_EXT) {
            if (name.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    public static String readableSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double value = bytes;
        String[] units = {"KB", "MB", "GB"};
        int unitIndex = -1;
        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }
        return String.format(Locale.ROOT, "%.2f %s", value, units[unitIndex]);
    }

    public static File ensureDirectory(File directory) throws IOException {
        if (directory == null) {
            throw new IOException("输出目录为空");
        }
        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }
        if (!directory.isDirectory() || !directory.canWrite()) {
            throw new IOException("输出目录不可写: " + directory.getAbsolutePath());
        }
        return directory;
    }

    public static File resolveUniqueFile(File directory, String baseName, String extension, boolean overwrite) {
        String safeBase = baseName;
        File candidate = new File(directory, safeBase + "." + extension);
        if (overwrite || !candidate.exists()) {
            return candidate;
        }
        int index = 1;
        while (candidate.exists()) {
            candidate = new File(directory, safeBase + "_" + index + "." + extension);
            index++;
        }
        return candidate;
    }

    public static String getBaseName(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot);
        }
        return name;
    }

    public static String getExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            return name.substring(dot + 1);
        }
        return "";
    }

    public static long sizeOf(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            return 0L;
        }
    }
}
