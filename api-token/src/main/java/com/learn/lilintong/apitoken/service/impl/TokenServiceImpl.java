package com.learn.lilintong.apitoken.service.impl;

import com.learn.lilintong.apitoken.common.CommonConstant;
import com.learn.lilintong.apitoken.common.CustomErrorCode;
import com.learn.lilintong.apitoken.exception.BaseRuntimeException;
import com.learn.lilintong.apitoken.model.TokenRefreshReqVo;
import com.learn.lilintong.apitoken.model.TokenReqVo;
import com.learn.lilintong.apitoken.model.TokenResVo;
import com.learn.lilintong.apitoken.service.ITokenService;
import com.learn.lilintong.apitoken.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author lilintong
 * @create 2020/7/22
 */
@Slf4j
@Service
public class TokenServiceImpl implements ITokenService {

    @Value("${lilintong.token_expires}")
    private Integer tokenExpiresTime;

    @Value("${lilintong.appid}")
    private String appId;

    @Value("${lilintong.password}")
    private String password;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public TokenResVo getToken(TokenReqVo tokenReqVo) {
        return getToken(tokenReqVo.getAppid(), tokenReqVo.getPassword());
    }

    @Override
    public TokenResVo refreshToken(TokenRefreshReqVo tokenRefreshReqVo) {
        return getToken(tokenRefreshReqVo.getAppid(), tokenRefreshReqVo.getPassword());
    }

    /**
     * 生成JWT token，存到redis
     *
     * @param appId
     * @param password
     * @return
     */
    private TokenResVo getToken(String appId, String password) {
        String token;
        if (!this.appId.equals(appId) || !this.password.equals(password)) {
            log.error("应用ID或者密码错误,appId{}, password{}", appId, password);
            throw new BaseRuntimeException(CustomErrorCode.ILLEGAL_REQUEST);
        }
        try {

            int expiresTime = tokenExpiresTime * 60 + 5;
            token = JwtUtil.generateToken(appId, appId, expiresTime);
            redisTemplate.opsForValue().set(CommonConstant.REDIS_TOKEN_CACHE + appId, token, expiresTime, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new BaseRuntimeException(CustomErrorCode.SERVICE_ERROR);
        }
        TokenResVo tokenResVo = new TokenResVo();
        tokenResVo.setExpires(tokenExpiresTime.toString());
        tokenResVo.setToken(token);
        tokenResVo.setMessage(CustomErrorCode.SUCCESS.getMessage());
        tokenResVo.setResult(CustomErrorCode.SUCCESS.getCode());
        return tokenResVo;
    }
}
