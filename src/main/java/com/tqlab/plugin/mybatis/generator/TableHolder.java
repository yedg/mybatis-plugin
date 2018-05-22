/**
 *
 */
package com.tqlab.plugin.mybatis.generator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lijun
 */
public class TableHolder {

    private static ThreadLocal<Map<String, String>> TABLE = new ThreadLocal<Map<String, String>>();

    public static void addTable(String name, String alias) {
        Map<String, String> map = TABLE.get();
        if (null == map) {
            map = new ConcurrentHashMap<String, String>(8);
            TABLE.set(map);
        }
        map.put(name, alias);
    }

    public static String getTableAlias(String name) {
        Map<String, String> map = TABLE.get();
        if (null != map) {
            return map.get(name);
        }
        return null;
    }

    public static void clear() {
        TABLE.remove();
    }
}
