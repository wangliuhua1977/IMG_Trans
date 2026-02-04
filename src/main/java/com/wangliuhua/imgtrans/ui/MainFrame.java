package com.wangliuhua.imgtrans.ui;

import com.wangliuhua.imgtrans.model.ConversionConfig;
import com.wangliuhua.imgtrans.model.ConversionResult;
import com.wangliuhua.imgtrans.model.ImageItem;
import com.wangliuhua.imgtrans.model.OutputFormat;
import com.wangliuhua.imgtrans.model.ScaleMode;
import com.wangliuhua.imgtrans.service.ConversionService;
import com.wangliuhua.imgtrans.service.ImageService;
import com.wangliuhua.imgtrans.util.FileUtil;
import com.wangliuhua.imgtrans.util.LogUtil;
import com.wangliuhua.imgtrans.util.PreferencesUtil;
import com.wangliuhua.imgtrans.util.SizeUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private final Logger logger = LogUtil.getLogger();
    private final FileTableModel tableModel = new FileTableModel();
    private final JTable table = new JTable(tableModel);
    private final PreviewPanel previewPanel = new PreviewPanel();
    private final JComboBox<OutputFormat> outputFormatBox = new JComboBox<>(OutputFormat.values());
    private final JCheckBox recursiveBox = new JCheckBox("递归子目录");
    private final JCheckBox overwriteBox = new JCheckBox("允许覆盖");
    private final JCheckBox unifiedOutputBox = new JCheckBox("统一输出目录");
    private final JCheckBox appendSizeBox = new JCheckBox("文件名追加尺寸后缀");
    private final JTextField outputDirField = new JTextField();
    private final JTextField customSizeField = new JTextField();
    private final JCheckBox[] sizeChecks = new JCheckBox[] {
            new JCheckBox("16"), new JCheckBox("24"), new JCheckBox("32"), new JCheckBox("48"),
            new JCheckBox("64"), new JCheckBox("128"), new JCheckBox("256")
    };
    private final JToggleButton logToggle = new JToggleButton("日志区", true);
    private final JTextArea logArea = new JTextArea();
    private final JScrollPane logScroll = new JScrollPane(logArea);
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel statusLabel = new JLabel("就绪");
    private final JButton startButton = new JButton("开始转换");
    private final JButton cancelButton = new JButton("取消");
    private final JButton openOutputButton = new JButton("打开输出目录");
    private final JButton pickLogDirButton = new JButton("日志目录");
    private final JComboBox<ScaleMode> scaleModeBox = new JComboBox<>(ScaleMode.values());
    private Color backgroundColor = Color.WHITE;
    private ConversionWorker worker;
    private ConversionService conversionService;
    private File lastOutputDir;

    public MainFrame() {
        setTitle("IMG_Trans 图片转图标");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        buildUi();
        bindActions();
        restorePreferences();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                persistPreferences();
                if (worker != null) {
                    worker.cancelConversion();
                }
            }
        });
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton addFileButton = new JButton("添加文件");
        JButton addFolderButton = new JButton("添加文件夹");
        JButton removeButton = new JButton("移除");
        JButton clearButton = new JButton("清空");
        JButton outputDirButton = new JButton("选择输出目录");
        toolBar.add(addFileButton);
        toolBar.add(addFolderButton);
        toolBar.add(removeButton);
        toolBar.add(clearButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("输出格式: "));
        toolBar.add(outputFormatBox);
        toolBar.addSeparator();
        toolBar.add(outputDirButton);
        toolBar.add(Box.createHorizontalStrut(8));
        outputDirField.setPreferredSize(new Dimension(240, 24));
        outputDirField.setEditable(false);
        toolBar.add(outputDirField);
        toolBar.addSeparator();
        toolBar.add(startButton);
        toolBar.add(cancelButton);
        toolBar.addSeparator();
        toolBar.add(openOutputButton);
        toolBar.addSeparator();
        toolBar.add(pickLogDirButton);
        add(toolBar, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        table.setRowHeight(24);
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, previewPanel);
        splitPane.setDividerLocation(720);
        add(splitPane, BorderLayout.CENTER);

        JPanel optionsPanel = buildOptionsPanel();
        JPanel statusPanel = new JPanel(new BorderLayout());
        progressBar.setStringPainted(true);
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(optionsPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logScroll.setPreferredSize(new Dimension(100, 160));
        JPanel logContainer = new JPanel(new BorderLayout());
        logContainer.add(logToggle, BorderLayout.NORTH);
        logContainer.add(logScroll, BorderLayout.CENTER);
        bottomPanel.add(logContainer, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        LogUtil.attachTextArea(logArea);

        cancelButton.setEnabled(false);
        openOutputButton.setEnabled(false);

        addFileButton.addActionListener(event -> chooseFiles());
        addFolderButton.addActionListener(event -> chooseFolder());
        removeButton.addActionListener(event -> removeSelected());
        clearButton.addActionListener(event -> clearAll());
        outputDirButton.addActionListener(event -> chooseOutputDirectory());
    }

    private JPanel buildOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBorder(BorderFactory.createTitledBorder("尺寸选择"));
        for (JCheckBox checkBox : sizeChecks) {
            sizePanel.add(checkBox);
        }
        sizePanel.add(new JLabel("自定义: "));
        customSizeField.setPreferredSize(new Dimension(200, 24));
        sizePanel.add(customSizeField);
        sizePanel.add(new JLabel("(多个用逗号分隔)"));

        JPanel scalePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scalePanel.setBorder(BorderFactory.createTitledBorder("缩放模式"));
        scalePanel.add(new JLabel("模式: "));
        scalePanel.add(scaleModeBox);
        JButton colorButton = new JButton("背景色");
        scalePanel.add(colorButton);

        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsRow.setBorder(BorderFactory.createTitledBorder("输出与行为"));
        optionsRow.add(recursiveBox);
        optionsRow.add(overwriteBox);
        optionsRow.add(unifiedOutputBox);
        optionsRow.add(appendSizeBox);

        optionsPanel.add(sizePanel);
        optionsPanel.add(scalePanel);
        optionsPanel.add(optionsRow);

        colorButton.addActionListener(event -> chooseBackgroundColor());

        return optionsPanel;
    }

    private void bindActions() {
        startButton.addActionListener(this::startConversion);
        cancelButton.addActionListener(event -> cancelConversion());
        openOutputButton.addActionListener(event -> openOutputDirectory());
        pickLogDirButton.addActionListener(event -> chooseLogDirectory());

        logToggle.addActionListener(event -> toggleLogPanel());

        table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int row = table.getSelectedRow();
            ImageItem item = tableModel.getItem(row);
            if (item != null) {
                loadPreview(item);
            }
        });

        setTransferHandler(new DropHandler(this::handleDroppedFiles));
    }

    private void chooseFiles() {
        JFileChooser chooser = createImageFileChooser();
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            if (files != null) {
                List<File> list = List.of(files);
                PreferencesUtil.saveLastInputDir(chooser.getCurrentDirectory().getAbsolutePath());
                addFiles(list, recursiveBox.isSelected());
            }
        }
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            PreferencesUtil.saveLastInputDir(folder.getAbsolutePath());
            addFiles(List.of(folder), recursiveBox.isSelected());
        }
    }

    private void addFiles(List<File> files, boolean recursive) {
        List<ImageItem> newItems = new ArrayList<>();
        for (File file : files) {
            for (File imageFile : FileUtil.listImages(file, recursive)) {
                if (tableModel.getItems().stream().anyMatch(item -> item.getFile().equals(imageFile))) {
                    continue;
                }
                try {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image == null) {
                        logger.warning("无法读取图片: " + imageFile.getName());
                        continue;
                    }
                    String format = FileUtil.getExtension(imageFile).toUpperCase();
                    long sizeBytes = FileUtil.sizeOf(imageFile.toPath());
                    newItems.add(new ImageItem(imageFile, format, sizeBytes, image.getWidth(), image.getHeight()));
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "读取失败: " + imageFile.getName() + " - " + ex.getMessage(), ex);
                }
            }
        }
        if (newItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到可用图片文件", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        tableModel.addItems(newItems);
    }

    private void removeSelected() {
        int[] rows = table.getSelectedRows();
        tableModel.removeRows(rows);
        previewPanel.clearPreview();
    }

    private void clearAll() {
        tableModel.clear();
        previewPanel.clearPreview();
    }

    private void chooseOutputDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            outputDirField.setText(dir.getAbsolutePath());
            lastOutputDir = dir;
            PreferencesUtil.saveLastOutputDir(dir.getAbsolutePath());
        }
    }

    private void chooseBackgroundColor() {
        Color color = JColorChooser.showDialog(this, "选择背景色", backgroundColor);
        if (color != null) {
            backgroundColor = color;
            PreferencesUtil.saveBackgroundColor(color);
        }
    }

    private void chooseLogDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            LogUtil.setLogDirectory(dir);
            PreferencesUtil.saveLogDir(dir.getAbsolutePath());
            logger.info("日志目录已更新: " + dir.getAbsolutePath());
        }
    }

    private void startConversion(ActionEvent event) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "请先添加图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ConversionConfig config = buildConfig();
        if (config == null) {
            return;
        }
        conversionService = new ConversionService();
        worker = new ConversionWorker(config, tableModel.getItems());
        worker.execute();
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        progressBar.setValue(0);
        statusLabel.setText("转换中...");
    }

    private ConversionConfig buildConfig() {
        ConversionConfig config = new ConversionConfig();
        config.setOutputFormat((OutputFormat) outputFormatBox.getSelectedItem());
        config.setRecursive(recursiveBox.isSelected());
        config.setOverwrite(overwriteBox.isSelected());
        config.setUnifiedOutput(unifiedOutputBox.isSelected());
        config.setAppendSizeSuffix(appendSizeBox.isSelected());
        config.setScaleMode((ScaleMode) scaleModeBox.getSelectedItem());
        config.setBackgroundColor(backgroundColor);

        Set<Integer> sizes = new LinkedHashSet<>();
        for (JCheckBox checkBox : sizeChecks) {
            if (checkBox.isSelected()) {
                sizes.add(Integer.parseInt(checkBox.getText()));
            }
        }
        try {
            sizes.addAll(SizeUtil.parseCustomSizes(customSizeField.getText()));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "尺寸错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (sizes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请至少选择一个尺寸", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        config.getSizes().addAll(sizes);

        if (config.isUnifiedOutput()) {
            if (outputDirField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "请选择统一输出目录", "提示", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            File outputDir = new File(outputDirField.getText());
            try {
                FileUtil.ensureDirectory(outputDir);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "输出目录不可写: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            config.setUnifiedOutputDir(outputDir);
        }
        return config;
    }

    private void cancelConversion() {
        if (worker != null) {
            worker.cancelConversion();
            statusLabel.setText("已取消");
        }
    }

    private void openOutputDirectory() {
        if (lastOutputDir == null) {
            return;
        }
        try {
            java.awt.Desktop.getDesktop().open(lastOutputDir);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "打开输出目录失败: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "无法打开输出目录", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleLogPanel() {
        boolean visible = logToggle.isSelected();
        logToggle.setText(visible ? "日志区" : "日志区(已隐藏)");
        logScroll.setVisible(visible);
        SwingUtilities.invokeLater(this::revalidate);
    }

    private void loadPreview(ImageItem item) {
        SwingWorker<BufferedImage, Void> previewWorker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return ImageService.read(item.getFile());
            }

            @Override
            protected void done() {
                try {
                    BufferedImage image = get();
                    previewPanel.updatePreview(image, collectPreviewSizes(), (ScaleMode) scaleModeBox.getSelectedItem(), backgroundColor);
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "预览加载失败: " + ex.getMessage(), ex);
                }
            }
        };
        previewWorker.execute();
    }

    private Set<Integer> collectPreviewSizes() {
        Set<Integer> sizes = new LinkedHashSet<>();
        for (JCheckBox checkBox : sizeChecks) {
            if (checkBox.isSelected()) {
                sizes.add(Integer.parseInt(checkBox.getText()));
            }
        }
        try {
            sizes.addAll(SizeUtil.parseCustomSizes(customSizeField.getText()));
        } catch (IllegalArgumentException ex) {
            logger.warning("自定义尺寸解析失败: " + ex.getMessage());
        }
        if (sizes.isEmpty()) {
            sizes.add(128);
        }
        return sizes;
    }

    private void restorePreferences() {
        outputFormatBox.setSelectedItem(PreferencesUtil.loadOutputFormat());
        recursiveBox.setSelected(PreferencesUtil.loadRecursive());
        overwriteBox.setSelected(PreferencesUtil.loadOverwrite());
        unifiedOutputBox.setSelected(PreferencesUtil.loadUnifiedOutput());
        appendSizeBox.setSelected(PreferencesUtil.loadAppendSizeSuffix());
        scaleModeBox.setSelectedItem(PreferencesUtil.loadScaleMode());
        backgroundColor = PreferencesUtil.loadBackgroundColor();
        Set<Integer> sizes = PreferencesUtil.loadSizes();
        for (JCheckBox checkBox : sizeChecks) {
            checkBox.setSelected(sizes.contains(Integer.parseInt(checkBox.getText())));
        }
        String lastOutput = PreferencesUtil.loadLastOutputDir();
        if (!lastOutput.isBlank()) {
            outputDirField.setText(lastOutput);
            lastOutputDir = new File(lastOutput);
        }
        String logDir = PreferencesUtil.loadLogDir();
        if (!logDir.isBlank()) {
            LogUtil.setLogDirectory(new File(logDir));
        }
    }

    private void persistPreferences() {
        PreferencesUtil.saveOutputFormat((OutputFormat) outputFormatBox.getSelectedItem());
        PreferencesUtil.saveRecursive(recursiveBox.isSelected());
        PreferencesUtil.saveOverwrite(overwriteBox.isSelected());
        PreferencesUtil.saveUnifiedOutput(unifiedOutputBox.isSelected());
        PreferencesUtil.saveAppendSizeSuffix(appendSizeBox.isSelected());
        PreferencesUtil.saveScaleMode((ScaleMode) scaleModeBox.getSelectedItem());
        PreferencesUtil.saveBackgroundColor(backgroundColor);
        PreferencesUtil.saveSizes(collectPreviewSizes());
    }

    private JFileChooser createImageFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("图片文件", "png", "jpg", "jpeg", "bmp", "gif", "tif", "tiff", "webp"));
        String lastInput = PreferencesUtil.loadLastInputDir();
        if (!lastInput.isBlank()) {
            chooser.setCurrentDirectory(new File(lastInput));
        }
        return chooser;
    }

    private void handleDroppedFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        addFiles(files, recursiveBox.isSelected());
    }

    private final class ConversionWorker extends SwingWorker<ConversionResult, Integer> {
        private final ConversionConfig config;
        private final List<ImageItem> items;

        private ConversionWorker(ConversionConfig config, List<ImageItem> items) {
            this.config = config;
            this.items = items;
        }

        @Override
        protected ConversionResult doInBackground() {
            return conversionService.convert(items, config, (current, total) -> publish(current * 100 / total));
        }

        @Override
        protected void process(List<Integer> chunks) {
            if (chunks.isEmpty()) {
                return;
            }
            int value = chunks.get(chunks.size() - 1);
            progressBar.setValue(value);
        }

        @Override
        protected void done() {
            try {
                ConversionResult result = get();
                showResult(result);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "转换失败: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(MainFrame.this, "转换失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } finally {
                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }

        public void cancelConversion() {
            cancel(true);
            conversionService.cancel();
        }
    }

    private void showResult(ConversionResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("成功: ").append(result.getSuccessCount())
                .append("  失败: ").append(result.getFailureCount())
                .append("  跳过: ").append(result.getSkipCount());
        if (!result.getFailureMessages().isEmpty()) {
            builder.append(System.lineSeparator()).append("失败详情:").append(System.lineSeparator());
            for (String message : result.getFailureMessages()) {
                builder.append("- ").append(message).append(System.lineSeparator());
            }
        }
        statusLabel.setText("转换完成");
        openOutputButton.setEnabled(!result.getOutputDirectories().isEmpty());
        if (!result.getOutputDirectories().isEmpty()) {
            lastOutputDir = result.getOutputDirectories().get(0);
        }
        JOptionPane.showMessageDialog(this, builder.toString(), "转换结果", JOptionPane.INFORMATION_MESSAGE);
    }
}
