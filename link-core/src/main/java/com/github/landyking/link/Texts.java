package com.github.landyking.link;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by landy on 2018/4/25.
 */
public class Texts {
    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    public static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    public static String firstHasText(Object... arguments) {
        for (Object one : arguments) {
            String tmp = toStr(one);
            if (hasText(tmp)) {
                return tmp;
            }
        }
        return null;
    }

    public static boolean hasText(String one) {
        return one != null && one.trim().length() > 0;
    }

    public static String toStr(Object obj, String def) {
        return obj == null ? def : obj.toString();
    }

    public static String toStr(Object obj) {
        return toStr(obj, null);
    }

    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static boolean isComplexLabel(String label) {
        if (label.indexOf('(') != -1) {
            return true;
        }
        return false;
    }

    public static String inline(String sql) {
        sql=sql.replace('\r',' ');
        sql=sql.replace('\n',' ');
        return sql;
    }
}
