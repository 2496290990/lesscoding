package net.lesscoding.util;

import java.io.File;

/**
 * @author eleven
 * @date 2021/7/30 13:18
 * @apiNote 文件工具类
 */
public class FileUtil {

    public static String getBasePackage(String str){
        str = str.replace(File.separator, ".");
        //匹配maven目录
        String[] splitArr = str.split(str.contains("java.") ? "java." : "src.");
        return splitArr[1].replace(File.separator, ".");
    }

    public static void main(String[] args) {
        System.out.println(File.separator);
        System.out.println(getBasePackage("E:\\lesscoding\\src\\main\\java\\com\\lesscoding"));
    }
}

