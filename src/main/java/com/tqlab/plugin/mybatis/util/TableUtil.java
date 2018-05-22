/**
 *
 */
package com.tqlab.plugin.mybatis.util;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lijun
 */
public class TableUtil {

    public static String getObjectName(String tableName, String tablePrefix) {
        if (null == tableName || "".equals(tableName.trim())) {
            return null;
        }

        String prefix = tablePrefix;
        if (StringUtils.isNotBlank(prefix) && tableName.toLowerCase().startsWith(prefix)) {
            String temp = tableName.substring(prefix.length()).trim();
            if (temp.startsWith("_")) {
                temp = temp.substring(1);
            }

            tableName = temp;
        }

        int index = tableName.indexOf(" ");
        while (index != -1) {
            if (index + 1 >= tableName.length() || index + 2 >= tableName.length()) {
                tableName = tableName.replace(" ", "");
                break;
            }
            String s1 = tableName.substring(index + 1, index + 2);
            String s2 = s1.toUpperCase();
            tableName = tableName.replace(" " + s1, "" + s2);
            index = tableName.indexOf(" ");
        }
        //
        index = tableName.indexOf("_");
        while (index != -1) {
            if (index + 1 >= tableName.length() || index + 2 >= tableName.length()) {
                tableName = tableName.replace("_", "");
                break;
            }
            String s1 = tableName.substring(index + 1, index + 2);
            String s2 = s1.toUpperCase();
            tableName = tableName.replace("_" + s1, "" + s2);
            index = tableName.indexOf("_");
        }
        if (tableName.length() == 0) {
            return null;
        }
        String s = tableName.substring(0, 1).toUpperCase() + tableName.substring(1, tableName.length());
        return s;
    }

    public static String getTableName(final String tableName) {
        String name = tableName.trim();

        final Pattern pattern = Pattern.compile("^[^a-zA-Z0-9](.)+[^a-zA-Z0-9]$");
        final Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            name = name.substring(1, name.length() - 1);
        }
        return name;
    }
}
