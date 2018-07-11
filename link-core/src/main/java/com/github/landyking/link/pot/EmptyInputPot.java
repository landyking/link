package com.github.landyking.link.pot;

import com.github.landyking.link.InputPot;

import java.util.HashMap;

/**
 * Created by landy on 2018/7/11.
 */
public class EmptyInputPot extends HashMap<String, String> implements InputPot {
    @Override
    public String getInputParamText(String name) {
        return get(name);
    }
}
