package com.ltgds.mypush.utils;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author Li Guoteng
 * @data 2023/8/16
 * @description
 */
public class ConcurrentHashMapUtils {

    private static boolean IS_JAVA8;

    static {
        try {
            IS_JAVA8 = System.getProperty("java.version").startsWith("1.8.");
        } catch (Exception e) {
            IS_JAVA8 = true;
        }
    }

    /**
     * Java8 ConcurrentHashMap#computeIfAbsent 存在性能问题的临时解决方案
     * @param map
     * @param key
     * @param func
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> V computeIfAbsent(ConcurrentMap<K, V> map, K key, Function<? super K, ? extends V> func) {
        if (IS_JAVA8) {
            V v = map.get(key);
            if (v == null) {
                v = map.computeIfAbsent(key, func);
            }
            return v;
        } else {
            return map.computeIfAbsent(key, func);
        }
    }
}
