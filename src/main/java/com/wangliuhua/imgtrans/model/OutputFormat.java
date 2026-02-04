package com.wangliuhua.imgtrans.model;

public enum OutputFormat {
    PNG("PNG"),
    ICO("ICO");

    private final String label;

    OutputFormat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
