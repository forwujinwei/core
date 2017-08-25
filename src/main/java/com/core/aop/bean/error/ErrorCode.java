 package com.core.aop.bean.error;
 
 import org.springframework.util.StringUtils;

import com.core.aop.bean.Strings;

 
 public abstract interface ErrorCode
 {
   public abstract int getValue();
   
   public abstract String getName();
   
   public static String[] getRequiredFields()
   {
     return null;
   }
   
   public static String[] toArray(String keys)
   {
     if (StringUtils.isEmpty(keys)) {
       return null;
     }
     return Strings.splitWithTrim(keys);
   }
 }
