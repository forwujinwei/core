 package com.core.aop.restpack;
 
 import com.core.aop.bean.Classes;
import com.core.aop.bean.Strings;
import com.fasterxml.jackson.databind.ObjectMapper;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.UUID;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.aspectj.lang.JoinPoint;
 import org.aspectj.lang.annotation.After;
 import org.aspectj.lang.annotation.AfterThrowing;
 import org.aspectj.lang.annotation.Aspect;
 import org.aspectj.lang.annotation.Before;
 import org.aspectj.lang.annotation.Pointcut;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.springframework.core.annotation.Order;
 import org.springframework.stereotype.Component;
 import org.springframework.web.context.request.RequestAttributes;
 import org.springframework.web.context.request.RequestContextHolder;
 import org.springframework.web.context.request.ServletRequestAttributes;
 
 @Aspect
 @Order(1)
 @Component
 public class RestPackAspect
 {
   private static final Logger log = LoggerFactory.getLogger(RestPackAspect.class);
   private static final ThreadLocal<String> bufferRequestId = new ThreadLocal();
   private static final ThreadLocal<Long> bufferBeginTime = new ThreadLocal();
   private static final ThreadLocal<Exception> bufferException = new ThreadLocal();
   private final ObjectMapper objectMapper;
   
   public static final boolean isRestPack()
   {
     return bufferRequestId.get() != null;
   }
   
   public static final String getRequestId()
   {
     return (String)bufferRequestId.get();
   }
   
   public static final Long getBeginTime()
   {
     return (Long)bufferBeginTime.get();
   }
   
   public static final Exception getException()
   {
     return (Exception)bufferException.get();
   }
   
   public RestPackAspect(ObjectMapper objectMapper)
   {
     this.objectMapper = objectMapper;
   }
   
   public ObjectMapper getObjectMapper()
   {
     return this.objectMapper;
   }
   
   @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
   public void httpResultPackAspect() {}
   
   @After("httpResultPackAspect()")
   public void doAfter(JoinPoint point)
   {
     if (isRestPack())
     {
       RequestAttributes servlet = RequestContextHolder.getRequestAttributes();
       HttpServletResponse response = ((ServletRequestAttributes)servlet).getResponse();
       response.setCharacterEncoding("UTF-8");
       response.setHeader("Content-Type", "text/json;charset=UTF-8");
     }
   }
   
   @Before("httpResultPackAspect()")
   public void doBefore(JoinPoint point)
   {
     ServletRequestAttributes servlet = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
     HttpServletRequest request = servlet.getRequest();
     String requestId = generateRequestId();
     String requestPath = request.getRequestURI();
     MDC.put("requestId", requestId);
     MDC.put("requestPath", requestPath);
     if (log.isInfoEnabled())
     {
       Map<String, Object> params = new HashMap();
       Enumeration<String> it = request.getParameterNames();
       while (it.hasMoreElements())
       {
         String key = (String)it.nextElement();
         String value = request.getParameter(key);
         if (value != null) {
           params.put(key, value);
         }
         String[] values = request.getParameterValues(key);
         if ((values != null) && (values.length > 1)) {
           params.put(key, values);
         }
       }
       log.info("request '{}' begin, params:\n{}", requestPath, Strings.toString(params));
     }
     clearThreadLocal();
     
 
     Object target = point.getTarget();
     Class<?> clazz = Classes.getTargetClass(target);
     RestPackController pack = (RestPackController)clazz.getAnnotation(RestPackController.class);
     if (pack != null)
     {
       long beginTime = System.currentTimeMillis();
       bufferBeginTime.set(Long.valueOf(beginTime));
       bufferRequestId.set(requestId);
     }
   }
   
   @AfterThrowing(pointcut="httpResultPackAspect()", throwing="e")
   public void handleThrowing(Exception e)
   {
     if (log.isInfoEnabled()) {
       log.info("handle throwed exception[{}]: {}", e.getClass().getName(), e.getMessage());
     }
     bufferException.set(e);
   }
   
   String generateRequestId()
   {
     return UUID.randomUUID().toString().replace("-", "");
   }
   
   static void clearThreadLocal()
   {
     bufferRequestId.remove();
     bufferBeginTime.remove();
     bufferException.remove();
   }
 }