package com.github.landyking.link;

/**
 * @author: landy
 * @date: 2018-07-29 23:05
 */
public class LocalDictItem {
    private final String code;
    private final String content;
    private final String marker;

    public LocalDictItem(String code, String content, String marker) {
        this.code = code;
        this.content = content;
        this.marker = marker;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public String getMarker() {
        return marker;
    }
}
