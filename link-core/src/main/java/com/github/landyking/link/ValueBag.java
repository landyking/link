package com.github.landyking.link;

import com.github.landyking.link.util.Texts;

/**
 * Created by landy on 2018/7/21.
 */
public class ValueBag {
    private Object originValue;
    private Object modifyValue;
    private final boolean internal;

    public ValueBag(boolean internal) {
        this.internal = internal;
    }

    public boolean isInternal() {
        return internal;
    }

    public Object getOriginValue() {
        return originValue;
    }

    public ValueBag setOriginValue(Object originValue) {
        this.originValue = originValue;
        return this;
    }

    public Object getModifyValue() {
        return modifyValue;
    }

    public ValueBag setModifyValue(Object modifyValue) {
        this.modifyValue = modifyValue;
        return this;
    }

    public Object getFinalValue() {
        return modifyValue == null ? originValue : modifyValue;
    }

    @Override
    public String toString() {
        return Texts.toStr(getFinalValue());
    }
}
