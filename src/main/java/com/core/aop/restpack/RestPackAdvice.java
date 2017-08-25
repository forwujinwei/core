 package com.core.aop.restpack;
 
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.CommonErrorCode;
import com.terran4j.commons.restpack.HttpResult;
 
 @Component
 @ControllerAdvice
 public class RestPackAdvice
   implements ResponseBodyAdvice<Object>
 {
   private static final Logger log = LoggerFactory.getLogger(RestPackAdvice.class);
   
   public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
   {
     return RestPackAspect.isRestPack();
   }
   
   public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response)
   {
     if (!RestPackAspect.isRestPack()) {
       return body;
     }
     HttpResult result = null;
     Exception e = RestPackAspect.getException();
     if (e != null)
     {
       BusinessException be = convert(e);
       result = HttpResult.fail(be);
     }
     else
     {
       result = HttpResult.success();
       if (body != null) {
         result.setData(body);
       }
     }
     setHttpResult(result);
     
     RestPackAspect.clearThreadLocal();
     if (log.isInfoEnabled()) {
       log.info("request '{}' end, response:\n{}", MDC.get("requestPath"), result);
     }
     return result;
   }
   
   void setHttpResult(HttpResult result)
   {
     String requestId = RestPackAspect.getRequestId();
     if (requestId != null) {
       result.setRequestId(requestId);
     }
     Long beginTime = RestPackAspect.getBeginTime();
     if (beginTime != null)
     {
       result.setServerTime(beginTime.longValue());
       long spendTime = System.currentTimeMillis() - beginTime.longValue();
       result.setSpendTime(spendTime);
     }
   }
   
   BusinessException convert(Throwable e)
   {
     if ((e instanceof BusinessException)) {
       return (BusinessException)e;
     }
     if ((e instanceof MissingServletRequestParameterException))
     {
       MissingServletRequestParameterException me = (MissingServletRequestParameterException)e;
       String paramKey = me.getParameterName();
       String paramType = me.getParameterType();
       if (log.isInfoEnabled()) {
         log.info("miss param, key = {}, type = {}", paramKey, paramType);
       }
       return new BusinessException(CommonErrorCode.NULL_PARAM).put("key", paramKey);
     }
     if ((e instanceof Error))
     {
       e.printStackTrace();
       log.error(e.getMessage(), e);
     }
     return new BusinessException(CommonErrorCode.UNKNOWN_ERROR, e);
   }
 }