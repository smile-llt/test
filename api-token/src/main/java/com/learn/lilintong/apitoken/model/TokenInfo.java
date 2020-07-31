package com.learn.lilintong.apitoken.model;

import lombok.Data;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Data
public class TokenInfo {
    /** token类型: api:0 、user:1 */
    private Integer tokenType;

    /** App 信息 */
    private AppInfo appInfo;

    /** 用户其他数据 */
    private UserInfo userInfo;
}
