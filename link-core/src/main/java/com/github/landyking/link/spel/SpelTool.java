package com.github.landyking.link.spel;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by landy on 2018/8/6.
 */
public class SpelTool {
    public static List<Object> newList(Integer size) {
        if (size == null) {
            size = 0;
        }
        ArrayList<Object> objects = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size; i++) {
            objects.add(i);
        }
        return objects;
    }
}
