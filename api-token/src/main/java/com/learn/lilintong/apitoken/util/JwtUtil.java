package com.learn.lilintong.apitoken.util;

import com.learn.lilintong.apitoken.common.CommonConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.util.Date;

public class JwtUtil {
	/***
	 * 生产token
	 * iss:jwt签发者 sub:jwt所面向的用户  Expiration:tocken过期时间  secret：秘钥 
	 * @return
	 */
	public static String generateToken(String sub, String appId, int expiration) {
		return Jwts.builder()
				.setSubject(sub)
				.claim(CommonConstant.APPID, appId)
				.setExpiration(generateExpirationDate(expiration))
				.signWith(SignatureAlgorithm.HS256, CommonConstant.HIK_SALT)
				.compact();
	}

	/***
	 *验证token 
	 * @return
	 */
	public static boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(CommonConstant.HIK_SALT).parseClaimsJws(token);
		} catch (JwtException ex) {
			return false;
		}
		return true;
	}

	/***
	 * 解析token
	 * @param token
	 */
	public static String parseToken(String token) throws JwtException {
		Claims claims = Jwts.parser().setSigningKey(CommonConstant.HIK_SALT).parseClaimsJws(token).getBody();
		String appId = String.valueOf(claims.get(CommonConstant.APPID));
		return appId;
	}

	/**
	 * 转换Date类型
	 *
	 * @param expiration
	 * @return
	 */
	private static Date generateExpirationDate(int expiration) {
		return DateTime.now().plusMinutes(expiration).toDate();
	}

//	public static void main(String[] args) {
//		String token = Jwts.builder()
//                .setSubject("123")
//                .claim(APPID,"123")
//                .setExpiration(DateTime.now().plusSeconds(30).toDate())
//                .signWith(SignatureAlgorithm.HS256, CommonConstant.HIK_SALT)
//                .compact();
//		System.out.println(token);
//		boolean res = JwtUtil.validateToken(token);
//		String s = JwtUtil.parseToken(token);
//		System.out.printf(s);
//	}
}
