package net.lesscoding.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author eleven
 * @date 2021/8/12 20:37
 * @apiNote 集合工具类，判断是否为空
 */
public class CollUtil {

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.size() == 0;
    }

    public static boolean isEmpty(Map map){
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(Collection collection){
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map map){
        return !isEmpty(map);
    }
}
