package com.demo.util;

import java.text.MessageFormat;
 
 
public class StringUtil {
 
    public static String format(String value, Object... paras) {
        return MessageFormat.format(value, paras);
    }
}