package com.sdc.util;

import java.io.File;

/**
 * 字符串工作类
 * Created by Administrator on 2016-05-17.
 */
public class StringUtil {
    /**
     * 文件夹分割符
     */
    public static String separatorChar = String.valueOf(File.separatorChar);
    /**
     * 换行符
     */
    public static String lineSeparator = System.getProperty("line.separator", "/r/n");

    /**
     * 第一个字母转换成大写
     * @param str   String类型    待转换的字符串
     * @return      String类型
     */
    public static String convertFirstCharUpper(String str){
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z'){
            ch[0] = (char)(ch[0] - 32);
        }
        return new String(ch);
    }
}
