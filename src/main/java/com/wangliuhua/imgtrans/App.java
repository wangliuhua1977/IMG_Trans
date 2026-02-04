package com.wangliuhua.imgtrans;

import com.formdev.flatlaf.FlatLightLaf;
import com.wangliuhua.imgtrans.ui.MainFrame;
import com.wangliuhua.imgtrans.util.ImageIOUtil;
import com.wangliuhua.imgtrans.util.LogUtil;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        LogUtil.initialize();
        ImageIO.scanForPlugins();
        ImageIOUtil.registerImageIO();
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
