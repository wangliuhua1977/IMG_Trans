package com.wangliuhua.imgtrans.ui;

import com.wangliuhua.imgtrans.model.ScaleMode;
import com.wangliuhua.imgtrans.service.ImageService;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Set;

public final class PreviewPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel originalLabel = new JLabel("未选择图片", SwingConstants.CENTER);
    private final JPanel thumbPanel = new JPanel();

    public PreviewPanel() {
        setLayout(new BorderLayout(8, 8));
        originalLabel.setPreferredSize(new Dimension(280, 280));
        originalLabel.setBorder(BorderFactory.createTitledBorder("原图预览"));

        thumbPanel.setBorder(BorderFactory.createTitledBorder("目标尺寸预览"));
        thumbPanel.setLayout(new GridLayout(0, 3, 8, 8));
        JScrollPane scrollPane = new JScrollPane(thumbPanel);
        scrollPane.setPreferredSize(new Dimension(280, 280));

        add(originalLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearPreview() {
        originalLabel.setText("未选择图片");
        originalLabel.setIcon(null);
        thumbPanel.removeAll();
        revalidate();
        repaint();
    }

    public void updatePreview(BufferedImage image, Set<Integer> sizes, ScaleMode mode, Color background) {
        if (image == null) {
            clearPreview();
            return;
        }
        originalLabel.setText("");
        originalLabel.setIcon(new ImageIcon(scaleToFit(image, 260, 260)));
        thumbPanel.removeAll();
        for (Integer size : sizes) {
            BufferedImage scaled = ImageService.scale(image, size, mode, background);
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setText(size + "x" + size);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setIcon(new ImageIcon(scaleToFit(scaled, 64, 64)));
            thumbPanel.add(label);
        }
        revalidate();
        repaint();
    }

    private Image scaleToFit(BufferedImage image, int maxW, int maxH) {
        int w = image.getWidth();
        int h = image.getHeight();
        double scale = Math.min(maxW / (double) w, maxH / (double) h);
        int newW = (int) Math.max(1, Math.round(w * scale));
        int newH = (int) Math.max(1, Math.round(h * scale));
        return image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }
}
