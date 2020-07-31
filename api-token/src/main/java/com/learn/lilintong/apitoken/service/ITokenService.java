package com.learn.lilintong.apitoken.service;

import com.learn.lilintong.apitoken.model.TokenRefreshReqVo;
import com.learn.lilintong.apitoken.model.TokenReqVo;
import com.learn.lilintong.apitoken.model.TokenResVo;

/**
 * @author lilintong
 * @create 2020/7/22
 */
public interface ITokenService {
    /**
     * 获取token
     *
     * @param tokenReqVo
     * @return
     */
    TokenResVo getToken(TokenReqVo tokenReqVo);

    /**
     * 刷新token
     *
     * @param tokenRefreshReqVo
     * @return
     */
    TokenResVo refreshToken(TokenRefreshReqVo tokenRefreshReqVo);
}
