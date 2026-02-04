package com.wangliuhua.imgtrans.util;

import com.wangliuhua.imgtrans.model.OutputFormat;
import com.wangliuhua.imgtrans.model.ScaleMode;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public final class PreferencesUtil {
    private static final Preferences PREFS = Preferences.userRoot().node("IMG_Trans");

    private PreferencesUtil() {
    }

    public static void saveOutputFormat(OutputFormat format) {
        PREFS.put("outputFormat", format.name());
    }

    public static OutputFormat loadOutputFormat() {
        String value = PREFS.get("outputFormat", OutputFormat.PNG.name());
        return OutputFormat.valueOf(value);
    }

    public static void saveLastInputDir(String path) {
        PREFS.put("lastInputDir", path == null ? "" : path);
    }

    public static String loadLastInputDir() {
        return PREFS.get("lastInputDir", "");
    }

    public static void saveLastOutputDir(String path) {
        PREFS.put("lastOutputDir", path == null ? "" : path);
    }

    public static String loadLastOutputDir() {
        return PREFS.get("lastOutputDir", "");
    }

    public static void saveRecursive(boolean recursive) {
        PREFS.putBoolean("recursive", recursive);
    }

    public static boolean loadRecursive() {
        return PREFS.getBoolean("recursive", false);
    }

    public static void saveOverwrite(boolean overwrite) {
        PREFS.putBoolean("overwrite", overwrite);
    }

    public static boolean loadOverwrite() {
        return PREFS.getBoolean("overwrite", false);
    }

    public static void saveUnifiedOutput(boolean unified) {
        PREFS.putBoolean("unifiedOutput", unified);
    }

    public static boolean loadUnifiedOutput() {
        return PREFS.getBoolean("unifiedOutput", false);
    }

    public static void saveAppendSizeSuffix(boolean append) {
        PREFS.putBoolean("appendSizeSuffix", append);
    }

    public static boolean loadAppendSizeSuffix() {
        return PREFS.getBoolean("appendSizeSuffix", false);
    }

    public static void saveScaleMode(ScaleMode mode) {
        PREFS.put("scaleMode", mode.name());
    }

    public static ScaleMode loadScaleMode() {
        return ScaleMode.valueOf(PREFS.get("scaleMode", ScaleMode.FIT_PAD.name()));
    }

    public static void saveSizes(Set<Integer> sizes) {
        String joined = String.join(",", sizes.stream().map(String::valueOf).toList());
        PREFS.put("sizes", joined);
    }

    public static Set<Integer> loadSizes() {
        String value = PREFS.get("sizes", "16,24,32,48,64,128,256");
        Set<Integer> sizes = new LinkedHashSet<>();
        Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(Integer::parseInt)
                .forEach(sizes::add);
        return sizes;
    }

    public static void saveBackgroundColor(Color color) {
        if (color == null) {
            PREFS.remove("bgColor");
            return;
        }
        PREFS.putInt("bgColor", color.getRGB());
    }

    public static Color loadBackgroundColor() {
        int rgb = PREFS.getInt("bgColor", Color.WHITE.getRGB());
        return new Color(rgb, true);
    }

    public static void saveLogDir(String path) {
        PREFS.put("logDir", path == null ? "" : path);
    }

    public static String loadLogDir() {
        return PREFS.get("logDir", "");
    }
}
