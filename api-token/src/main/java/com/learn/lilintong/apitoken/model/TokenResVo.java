package com.learn.lilintong.apitoken.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Getter
@Setter
public class TokenResVo extends BaseResponse {
    @ApiModelProperty("token")
    private String token;
    @ApiModelProperty("过期时间")
    private String expires;
}