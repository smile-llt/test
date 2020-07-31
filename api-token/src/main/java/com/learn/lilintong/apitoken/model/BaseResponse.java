package com.learn.lilintong.apitoken.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Getter
@Setter
public class BaseResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息提示")
    private String message;
    @ApiModelProperty("是否成功（0失败 1 成功）")
    private String result;
    @ApiModelProperty("失败时错误码")
    private String errorcode;
}