 package com.core.aop.restpack;

import com.core.aop.bean.error.ErrorCode;

public enum RequestErrorCode
   implements ErrorCode
 {
   RESOURCE_NOT_FOUND(100001, "resource.not.found"),  AUTH_FAILED(100002, "auth.failed"),  ACCESS_FORBIDDEN(100003, "access.forbidden"),  INVALID_PARAM(100004, "invalid.param"),  NULL_PARAM(100005, "null.param"),  INTERNAL_ERROR(100006, "internal.error"),  RESPONSE_ERROR(100007, "response.error"),  BLACKLISTED_INPUT(100008, "blacklisted.input");
   
   private final int value;
   private final String name;
   
   private RequestErrorCode(int value, String name)
   {
     this.value = value;
     this.name = name;
   }
   
   public final int getValue()
   {
     return this.value;
   }
   
   public final String getName()
   {
     return this.name;
   }
 }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-restpack\1.0.8\terran4j-commons-restpack-1.0.8.jar
 * Qualified Name:     com.terran4j.commons.restpack.RequestErrorCode
 * JD-Core Version:    0.7.0.1
 */