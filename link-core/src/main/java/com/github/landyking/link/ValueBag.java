package com.github.landyking.link;

/**
 * Created by landy on 2018/7/21.
 */
public class ValueBag {
    private Object originValue;
    private Object modifyValue;

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
}
