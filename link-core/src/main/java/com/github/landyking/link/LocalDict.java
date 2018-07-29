package com.github.landyking.link;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;

/**
 * @author: landy
 * @date: 2018-07-29 23:04
 */
public class LocalDict {
    private final String name;
    private final String desc;
    private final LinkedHashMap<String, LocalDictItem> items = Maps.newLinkedHashMap();

    public LocalDict(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public LinkedHashMap<String, LocalDictItem> getItems() {
        return items;
    }
}
