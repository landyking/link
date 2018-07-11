package com.github.landyking.link;

import java.util.Collection;
import java.util.Map;

/**
 * Created by landy on 2018/7/11.
 */
public class LkTools {
    public static final boolean devMode = initDevMode();

    private static boolean initDevMode() {
        String value = System.getProperty("dm.inDevMode", "false");
        return isTrue(value);
    }

    public static boolean isFlagTrue(Integer flag) {
        return flag != null && flag.equals(1);
    }

    public static boolean isFlagFalse(Integer flag) {
        return flag == null || flag.equals(0);
    }

    public static boolean isTrue(String v) {
        return v != null && (v.equals("1")
                || v.equalsIgnoreCase("true")
                || v.equalsIgnoreCase("yes")
                || v.equalsIgnoreCase("y"));
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        }
        return false;
    }

    public static boolean isDevMode() {
        return devMode;
    }
}
