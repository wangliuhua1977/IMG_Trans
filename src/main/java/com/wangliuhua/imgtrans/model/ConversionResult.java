package com.wangliuhua.imgtrans.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversionResult {
    private int successCount;
    private int failureCount;
    private int skipCount;
    private final List<String> failureMessages = Collections.synchronizedList(new ArrayList<>());
    private final List<File> outputDirectories = Collections.synchronizedList(new ArrayList<>());

    public synchronized int getSuccessCount() {
        return successCount;
    }

    public synchronized void incrementSuccess() {
        successCount++;
    }

    public synchronized int getFailureCount() {
        return failureCount;
    }

    public synchronized void incrementFailure(String message) {
        failureCount++;
        failureMessages.add(message);
    }

    public synchronized int getSkipCount() {
        return skipCount;
    }

    public synchronized void incrementSkip(String message) {
        skipCount++;
        failureMessages.add(message);
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public List<File> getOutputDirectories() {
        return outputDirectories;
    }

    public void addOutputDirectory(File dir) {
        if (dir != null && !outputDirectories.contains(dir)) {
            outputDirectories.add(dir);
        }
    }
}
