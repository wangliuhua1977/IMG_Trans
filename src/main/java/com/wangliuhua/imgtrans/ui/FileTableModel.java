package com.wangliuhua.imgtrans.ui;

import com.wangliuhua.imgtrans.model.ImageItem;
import com.wangliuhua.imgtrans.util.FileUtil;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FileTableModel extends AbstractTableModel {
    private final List<ImageItem> items = new ArrayList<>();
    private final String[] columns = {"文件名", "尺寸", "格式", "大小", "路径"};

    public void addItems(List<ImageItem> newItems) {
        items.addAll(newItems);
        fireTableDataChanged();
    }

    public void removeRows(int[] rows) {
        if (rows == null || rows.length == 0) {
            return;
        }
        for (int i = rows.length - 1; i >= 0; i--) {
            items.remove(rows[i]);
        }
        fireTableDataChanged();
    }

    public void clear() {
        items.clear();
        fireTableDataChanged();
    }

    public List<ImageItem> getItems() {
        return new ArrayList<>(items);
    }

    public ImageItem getItem(int row) {
        if (row < 0 || row >= items.size()) {
            return null;
        }
        return items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ImageItem item = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getFile().getName();
            case 1 -> item.getWidth() + "x" + item.getHeight();
            case 2 -> item.getFormat();
            case 3 -> FileUtil.readableSize(item.getSizeBytes());
            case 4 -> item.getFile().getAbsolutePath();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
