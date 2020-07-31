package com.learn.lilintong.apitoken.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class TokenRefreshReqVo {
	@ApiModelProperty("厂商提供的应用ID")
	@NotEmpty(message = "应用ID不能为空")
	private String appid;

	@ApiModelProperty("厂商提供应用密码")
	@NotEmpty(message = "应用密码不能为空")
	private String password;

	@ApiModelProperty("授权token")
	@NotEmpty(message = "授权token不能为空")
	private String token;
}
