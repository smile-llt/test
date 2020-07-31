package com.learn.lilintong.apitoken.common;

/**
 * @author lilintong
 * @create 2020/4/2
 */
public enum CacheNameEnum {
    USER("user"),
    DEPARTMENT("department"),
    CROSS("cross"),
    REGION("region");

    String key;

    CacheNameEnum(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
