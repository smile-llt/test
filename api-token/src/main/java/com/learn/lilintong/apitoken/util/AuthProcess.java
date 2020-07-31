package com.learn.lilintong.apitoken.util;

import it.sauronsoftware.base64.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AuthProcess {

	public static String getAuthorization(String accesskey,String secretKey,String httpVerb,String contentMd5,
			String contentType,String date,String uri, String component, int encryptionAlgorithm){
		StringBuffer auth_data = new StringBuffer("");
		auth_data.append(component).append(" ");
		auth_data.append(accesskey).append(":");
		
		String signature = getSignature(secretKey,httpVerb,contentMd5,contentType,date,uri, encryptionAlgorithm);
		auth_data.append(signature);
		
		return auth_data.toString();
	}
	
	private static String getSignature(String secretKey,String httpVerb,String contentMd5,String contentType,String date,
			String uri, int encryptionAlgorithm){
		
		String StringToSign = getStringToSign(httpVerb,contentMd5,contentType,date,uri);

		byte[] byteData = Base64.encode(getHMACData(StringToSign,secretKey, encryptionAlgorithm));
		String encoderData = new String(byteData);
		return encoderData;
	}
	

	private static byte[] getHMACData(String data,String secretKey, int encryptionAlgorithm) {
		
		byte[] byteHMAC = null;
		String MAC_NAME = null;

		if (0 == encryptionAlgorithm) {
			MAC_NAME = "HmacSHA1";
		} else if (1 == encryptionAlgorithm) {
			MAC_NAME = "HmacSHA256";
		} else {
			return byteHMAC;
		}
		try{
			Mac mac = Mac.getInstance(MAC_NAME);
			SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), MAC_NAME);
			mac.init(spec);
	          
			byteHMAC = mac.doFinal(data.getBytes());
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return byteHMAC;
	}
	

	private static String getStringToSign (String httpVerb,String contentMd5,String contentType,String date, String uri){
		StringBuffer sign_data = new StringBuffer("");

		sign_data.append(httpVerb).append("\n");
		sign_data.append(contentMd5).append("\n");
		sign_data.append(contentType).append("\n");
		sign_data.append(date).append("\n");
		sign_data.append(uri);
		
		return sign_data.toString();
	}
}
