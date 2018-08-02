package com.github.landyking.link.converter;


/**
 * Created by landy on 2018/8/2.
 */
public class DatePack {
    private String srcFormat;
    private Object value;
    private String srcType;
    private String destType;
    private String destFormat;

    public DatePack(Object value, String srcType, String srcFormat, String destType, String destFormat) {
        this.srcFormat = srcFormat;
        this.value = value;
        this.srcType = srcType;
        this.destType = destType;
        this.destFormat = destFormat;
    }

    public DatePack() {
    }

    public String getSrcFormat() {
        return srcFormat;
    }

    public void setSrcFormat(String srcFormat) {
        this.srcFormat = srcFormat;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public String getDestFormat() {
        return destFormat;
    }

    public void setDestFormat(String destFormat) {
        this.destFormat = destFormat;
    }
}
