package com.learn.lilintong.apitoken.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lilintong
 * @create 2020/7/22
 */
public class StringFormatUtils {
    public StringFormatUtils() {
    }

    public static String getExceptionMessage(String message, Object[] args) {
        if (StringUtils.isNotEmpty(message)) {
            message = String.format(message.replaceAll("\\{\\}", "%s"), args);
        }

        return message;
    }
}
