package com.wangliuhua.imgtrans.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.TransferHandler;

public class DropHandler extends TransferHandler {
    private final DropListener listener;

    public DropHandler(DropListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        try {
            Transferable transferable = support.getTransferable();
            Object data = transferable.getTransferData(DataFlavor.javaFileListFlavor);
            if (!(data instanceof List<?> list)) {
                return false;
            }
            List<File> files = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof File file) {
                    files.add(file);
                }
            }
            if (files.isEmpty()) {
                return false;
            }
            listener.onFilesDropped(files);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public interface DropListener {
        void onFilesDropped(List<File> files);
    }
}
