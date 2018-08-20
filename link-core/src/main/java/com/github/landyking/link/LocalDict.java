package com.github.landyking.link;

import com.github.landyking.link.exception.LinkException;
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

    public int singleInt(String marker) throws LinkException {
        for (LocalDictItem localDictItem : items.values()) {
            if (marker.equals(localDictItem.getMarker())) {
                try {
                    return Integer.parseInt(localDictItem.getCode());
                } catch (Exception e) {
                    throw new LinkException("字典" + name + ":" + desc + "的字典项" + localDictItem.getCode() + "不是数值", e);
                }
            }
        }
        throw new LinkException("字典" + name + ":" + desc + "不包含marker为" + marker + "的字典项");
    }

    public String singleStr(String marker) throws LinkException {
        for (LocalDictItem localDictItem : items.values()) {
            if (marker.equals(localDictItem.getMarker())) {
                return localDictItem.getCode();
            }
        }
        throw new LinkException("字典" + name + ":" + desc + "不包含marker为" + marker + "的字典项");
    }
}
