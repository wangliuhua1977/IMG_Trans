package com.wangliuhua.imgtrans.util;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogUtil {
    private static final Logger LOGGER = Logger.getLogger("IMG_Trans");
    private static final String DEFAULT_LOG_DIR = System.getProperty("user.home") + File.separator + ".img_trans" + File.separator + "logs";
    private static Handler uiHandler;
    private static FileHandler fileHandler;

    private LogUtil() {
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void initialize() {
        LOGGER.setUseParentHandlers(false);
        setupFileHandler(new File(DEFAULT_LOG_DIR));
    }

    public static void attachTextArea(JTextArea textArea) {
        if (uiHandler != null) {
            LOGGER.removeHandler(uiHandler);
        }
        uiHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (!isLoggable(record)) {
                    return;
                }
                String message = new SimpleDateFormat("HH:mm:ss").format(new Date(record.getMillis()))
                        + " [" + record.getLevel().getName() + "] "
                        + record.getMessage() + System.lineSeparator();
                SwingUtilities.invokeLater(() -> textArea.append(message));
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        uiHandler.setLevel(Level.INFO);
        LOGGER.addHandler(uiHandler);
    }

    public static void setLogDirectory(File directory) {
        if (directory == null) {
            return;
        }
        setupFileHandler(directory);
    }

    private static void setupFileHandler(File directory) {
        try {
            Files.createDirectories(directory.toPath());
            if (fileHandler != null) {
                LOGGER.removeHandler(fileHandler);
                fileHandler.close();
            }
            File logFile = new File(directory, "img_trans.log");
            fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "日志文件初始化失败: " + ex.getMessage(), ex);
        }
    }

    private static final class SimpleFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(record.getMillis()));
            StringBuilder builder = new StringBuilder();
            builder.append(time).append(" [").append(record.getLevel().getName()).append("] ")
                    .append(record.getMessage());
            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(System.lineSeparator()).append(writer);
            }
            builder.append(System.lineSeparator());
            return builder.toString();
        }
    }
}
