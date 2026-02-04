package com.wangliuhua.imgtrans.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
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
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
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
