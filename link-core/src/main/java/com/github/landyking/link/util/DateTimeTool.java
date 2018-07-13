package com.github.landyking.link.util;

import com.google.common.primitives.Longs;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by landy on 2018/5/26.
 */
public class DateTimeTool {
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_ZH = "yyyy年MM月dd日";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM = "HH:mm";
    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    public static Date tryParse(String txt) {
        if (!Texts.hasText(txt)) {
            return null;
        }
        List<String> pts = Arrays.asList(YYYY_MM_DD_HH_MM_SS,YYYY_MM_DD_HH_MM, YYYY_MM_DD_HH, YYYY_MM_DD, YYYY_MM, HH_MM_SS, HH_MM, YYYYMMDDHHMMSS, YYYYMMDDHHMM, YYYYMMDDHH, YYYYMMDD, YYYYMM);
        for (String pt : pts) {
            try {
                Date parse = new SimpleDateFormat(pt).parse(txt);
                return parse;
            } catch (ParseException e) {
            }
        }
        Long aLong = Longs.tryParse(txt);
        if (aLong != null) {
            return new Date(aLong);
        }
        return null;
    }
    public static String fullText(Date time){
        long tmp = time.getTime();
        return fullText(tmp);
    }

    private static String fullText(long tmp) {
        return DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS).print(tmp);
    }
}
