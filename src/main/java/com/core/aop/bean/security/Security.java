package com.core.aop.bean.security;
 
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.aop.bean.error.BusinessException;
 
 public class Security
 {
   private static final Logger log = LoggerFactory.getLogger(Security.class);
   
   public static final AsymmetricKeys createAsymmetricKeys()
     throws BusinessException
   {
     return new AsymmetricKeys();
   }
   
   public static final AsymmetricKeys buildAsymmetricKeys(String publicKey, String privateKey)
     throws BusinessException
   {
     if (log.isInfoEnabled()) {
       log.info("build AsymmetricKeys, publicKey = {}, privateKey = {}", publicKey, privateKey);
     }
     return new AsymmetricKeys(publicKey, privateKey);
   }
   
   public static String signature(Map<String, String> data, String secretKey)
     throws BusinessException
   {
     return MD5Util.signature(data, secretKey);
   }
 }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.security.Security
 * JD-Core Version:    0.7.0.1
 */