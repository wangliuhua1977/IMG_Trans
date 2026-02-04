package com.wangliuhua.imgtrans.service;

import com.wangliuhua.imgtrans.model.ConversionConfig;
import com.wangliuhua.imgtrans.model.ConversionResult;
import com.wangliuhua.imgtrans.model.ImageItem;
import com.wangliuhua.imgtrans.model.OutputFormat;
import com.wangliuhua.imgtrans.util.FileUtil;
import com.wangliuhua.imgtrans.util.LogUtil;

import net.sf.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConversionService {
    private final Logger logger = LogUtil.getLogger();
    private ExecutorService executor;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public void cancel() {
        cancelled.set(true);
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public ConversionResult convert(List<ImageItem> items, ConversionConfig config, ProgressCallback callback) {
        cancelled.set(false);
        int threads = Math.min(4, Runtime.getRuntime().availableProcessors());
        executor = Executors.newFixedThreadPool(Math.max(1, threads));
        List<Future<Void>> futures = new ArrayList<>();
        ConversionResult result = new ConversionResult();
        int total = items.size();
        for (int i = 0; i < items.size(); i++) {
            int index = i;
            ImageItem item = items.get(i);
            futures.add(executor.submit(() -> {
                if (cancelled.get()) {
                    return null;
                }
                handleItem(item, config, result);
                callback.onProgress(index + 1, total);
                return null;
            }));
        }
        for (Future<Void> future : futures) {
            if (cancelled.get()) {
                break;
            }
            try {
                future.get();
            } catch (Exception ex) {
                logger.log(Level.WARNING, "任务执行异常: " + ex.getMessage(), ex);
            }
        }
        executor.shutdown();
        return result;
    }

    private void handleItem(ImageItem item, ConversionConfig config, ConversionResult result) {
        File source = item.getFile();
        logger.info("开始处理: " + source.getAbsolutePath());
        try {
            BufferedImage image = ImageService.read(source);
            boolean hasAlpha = ImageService.hasAlpha(image);
            Color background = hasAlpha ? null : config.getBackgroundColor();
            Set<Integer> sizes = config.getSizes();
            File outputDir = config.isUnifiedOutput() ? config.getUnifiedOutputDir() : source.getParentFile();
            FileUtil.ensureDirectory(outputDir);
            result.addOutputDirectory(outputDir);
            if (config.getOutputFormat() == OutputFormat.PNG) {
                for (Integer size : sizes) {
                    if (cancelled.get()) {
                        result.incrementSkip("取消: " + source.getName());
                        return;
                    }
                    BufferedImage scaled = ImageService.scale(image, size, config.getScaleMode(), background);
                    String baseName = FileUtil.getBaseName(source);
                    if (config.isAppendSizeSuffix()) {
                        baseName = baseName + "_" + size + "x" + size;
                    }
                    File outFile = FileUtil.resolveUniqueFile(outputDir, baseName, "png", config.isOverwrite());
                    boolean written = ImageIO.write(scaled, "png", outFile);
                    if (!written) {
                        throw new IOException("PNG写入失败: " + outFile.getName());
                    }
                }
            } else {
                List<BufferedImage> icoImages = new ArrayList<>();
                for (Integer size : sizes) {
                    if (cancelled.get()) {
                        result.incrementSkip("取消: " + source.getName());
                        return;
                    }
                    icoImages.add(ImageService.scale(image, size, config.getScaleMode(), background));
                }
                String baseName = FileUtil.getBaseName(source);
                File outFile = FileUtil.resolveUniqueFile(outputDir, baseName, "ico", config.isOverwrite());
                ICOEncoder.write(icoImages, outFile);
            }
            result.incrementSuccess();
            logger.info("处理完成: " + source.getName());
        } catch (IOException ex) {
            String message = source.getName() + ": " + ex.getMessage();
            result.incrementFailure(message);
            logger.log(Level.WARNING, message, ex);
        }
    }

    public interface ProgressCallback {
        void onProgress(int current, int total);
    }
}
