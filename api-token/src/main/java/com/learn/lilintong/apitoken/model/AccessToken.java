package com.learn.lilintong.apitoken.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Data
@AllArgsConstructor
public class AccessToken {

    /** token */
    private String token;

    /** 失效时间 */
    private Date expireTime;
}