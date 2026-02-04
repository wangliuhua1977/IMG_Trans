package com.wangliuhua.imgtrans.model;

public enum ScaleMode {
    FIT_PAD("等比缩放+透明补边"),
    FIT_CROP("等比缩放+居中裁剪"),
    STRETCH("强制拉伸");

    private final String label;

    ScaleMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
