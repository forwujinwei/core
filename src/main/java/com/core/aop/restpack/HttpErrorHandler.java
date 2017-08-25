 package com.core.aop.restpack;
 
 import javax.servlet.http.HttpServletRequest;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.http.HttpStatus;
 import org.springframework.web.bind.MissingServletRequestParameterException;
 import org.springframework.web.bind.annotation.ControllerAdvice;
 import org.springframework.web.bind.annotation.ExceptionHandler;
 import org.springframework.web.bind.annotation.ResponseBody;
 import org.springframework.web.bind.annotation.ResponseStatus;

import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.CommonErrorCode;
import com.terran4j.commons.restpack.HttpResult;
 
 @ControllerAdvice
 public class HttpErrorHandler
 {
   private static final Logger logger = LoggerFactory.getLogger(HttpErrorHandler.class);
   
   @ResponseBody
   @ExceptionHandler({Exception.class})
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public HttpResult handleAllException(Exception e, HttpServletRequest request)
   {
     logger.error("[Handled] unknown exception", e);
     return HttpResult.fail(new BusinessException(CommonErrorCode.UNKNOWN_ERROR, e));
   }
   
   @ResponseBody
   @ExceptionHandler({MissingServletRequestParameterException.class})
   @ResponseStatus(HttpStatus.OK)
   public HttpResult handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request)
   {
     if (logger.isInfoEnabled()) {
       logger.info("missing param: {}", e.getParameterName());
     }
     return HttpResult.fail(new BusinessException(CommonErrorCode.NULL_PARAM).put("key", e.getParameterName()));
   }
 }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-restpack\1.0.8\terran4j-commons-restpack-1.0.8.jar
 * Qualified Name:     com.terran4j.commons.restpack.HttpErrorHandler
 * JD-Core Version:    0.7.0.1
 */