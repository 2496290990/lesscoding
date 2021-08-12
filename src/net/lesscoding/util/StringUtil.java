package net.lesscoding.util;

import com.intellij.codeInsight.template.postfix.templates.SoutPostfixTemplate;
import org.jsoup.select.Evaluator;

import javax.xml.bind.SchemaOutputResolver;

/**
 * @author eleven
 * @date 2021/7/30 13:17
 * @apiNote 字符串工具类
 */
public class StringUtil {
    /** 下划线 */
    private static final String UNDERLINE = "_";

    /**
     * 驼峰转下划线工具类
     * @param prefix 前缀
     * @param str   表名或者是字段名
     * @return String
     * @apiNote  eg：
     *  camelToUnderLine(null,"tStuName")  ==> tUNDERLINEstuUNDERLINEname
     *  camelToUnderLine("t","tStuName")  ==> stuUNDERLINEname
     */
    public static String camelToUnderLine(String prefix,String str){
        str = removePrefix(prefix, str);
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if(aChar >= 'A' && aChar<= 'Z'){
                if(str.indexOf(aChar) != 0){
                    stringBuilder.append(UNDERLINE);
                }
                stringBuilder.append((char)(aChar + 32));
            }else {
                stringBuilder.append(aChar);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 下划线转驼峰 ，前缀可以不写  t_ 可以写成 t
     * @param prefix 前缀字符串
     * @param str   要转换的字符串
     * @return
     */
    public static String underLineToCamel(String prefix,String str){
        str = removePrefix(prefix, str);
        if(str.startsWith(UNDERLINE)){
            str = str.replaceFirst(UNDERLINE, "");
        }
        String[] splitArr = str.split(UNDERLINE);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : splitArr) {
            stringBuilder.append(firstToUpperCase(s));
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母大写
     * @param str 要转换的字符串
     * @return
     */
    public static String firstToUpperCase(String str){
        return (char)(str.charAt(0) - 32) + str.substring(1);
    }
    /**
     * 去除表前缀
     * @param prefix  前缀
     * @param str   要去除的字符串
     * @return
     */
    public static String removePrefix(String prefix,String str){
        return isNotEmpty(prefix) ? str.replaceFirst(prefix, "") : str;
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0 ;
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

}
