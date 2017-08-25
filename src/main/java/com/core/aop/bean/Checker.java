 package com.core.aop.bean;
 
 
 import org.springframework.util.StringUtils;

import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.CommonErrorCode;
 
 public class Checker
 {
   public static final void checkNotNull(String value, String key)
     throws BusinessException
   {
     if (StringUtils.isEmpty(value)) {
       throw 
         new BusinessException(CommonErrorCode.NULL_PARAM).put("key", key);
     }
   }
   
   public static final String checkLength(String value, int maxLenth, String key)
     throws BusinessException
   {
     if (StringUtils.isEmpty(value)) {
       return value;
     }
     value = value.trim();
     if (value.length() > maxLenth) {
       throw 
       
 
         new BusinessException(CommonErrorCode.PARAM_LENGTH_TOO_LONG).put("key", key).put("maxLength", Integer.valueOf(maxLenth)).put("acutalLength", Integer.valueOf(value.length()));
     }
     return value;
   }
 }
