package com.learn.lilintong.apitoken.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.learn.lilintong.apitoken.common.CommonConstant;
import com.learn.lilintong.apitoken.common.CustomErrorCode;
import com.learn.lilintong.apitoken.exception.BaseRuntimeException;
import com.learn.lilintong.apitoken.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@Slf4j
@Aspect
@Configuration
public class AuthTokenAspect {

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Around(value = "@annotation(com.learn.lilintong.apitoken.annotation.AuthToken)")
	public Object doBefore(ProceedingJoinPoint pjp) throws Throwable {
		Object[] arguments = pjp.getArgs();
		JSONObject jsonParameter = (JSONObject) JSON.toJSON(arguments[0]);
		String token = jsonParameter.getString(CommonConstant.TOKEN);
		if (StringUtils.isBlank(token)) {
			log.error("请求异常 token 为空");
			throw new BaseRuntimeException(CustomErrorCode.ILLEGAL_REQUEST);
		}
		String appId;
		try {
			appId = JwtUtil.parseToken(token);
		} catch (JwtException e) {
			log.error("token 解析失败", e);
			throw new BaseRuntimeException(CustomErrorCode.TOKEN_ERROR);
		}
		String redisToken = redisTemplate.opsForValue().get(CommonConstant.REDIS_TOKEN_CACHE + appId);
		if (StringUtils.isBlank(redisToken)) {
			log.error("token 失效, e:{}", token);
			throw new BaseRuntimeException(CustomErrorCode.TOKEN_ERROR);
		}
		if (!redisToken.equals(token)) {
			log.error("token 错误, e:{}", token);
			throw new BaseRuntimeException(CustomErrorCode.TOKEN_ERROR);
		}
		return pjp.proceed();
	}
}
