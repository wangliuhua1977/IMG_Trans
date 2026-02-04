package com.wangliuhua.imgtrans.model;

import java.awt.Color;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConversionConfig {
    private OutputFormat outputFormat;
    private boolean recursive;
    private boolean overwrite;
    private boolean unifiedOutput;
    private boolean appendSizeSuffix;
    private ScaleMode scaleMode;
    private Color backgroundColor;
    private File unifiedOutputDir;
    private final Set<Integer> sizes = new LinkedHashSet<>();

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isUnifiedOutput() {
        return unifiedOutput;
    }

    public void setUnifiedOutput(boolean unifiedOutput) {
        this.unifiedOutput = unifiedOutput;
    }

    public boolean isAppendSizeSuffix() {
        return appendSizeSuffix;
    }

    public void setAppendSizeSuffix(boolean appendSizeSuffix) {
        this.appendSizeSuffix = appendSizeSuffix;
    }

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public File getUnifiedOutputDir() {
        return unifiedOutputDir;
    }

    public void setUnifiedOutputDir(File unifiedOutputDir) {
        this.unifiedOutputDir = unifiedOutputDir;
    }

    public Set<Integer> getSizes() {
        return sizes;
    }
}
