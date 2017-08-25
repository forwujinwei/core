 package com.core.aop.bean.error;
 

 import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.core.aop.bean.value.RichProperties;
import com.terran4j.commons.util.error.ErrorReport;
 
 public class BusinessException
   extends Exception
 {
   private static final long serialVersionUID = 5988465338967853686L;
   private static final Map<Class<? extends ErrorCode>, ResourceBundle> bundles = new ConcurrentHashMap();
   private static final ResourceBundle NONEXISTENT_BUNDLE = new ResourceBundle()
   {
     public Enumeration<String> getKeys()
     {
       return null;
     }
     
     protected Object handleGetObject(String key)
     {
       return null;
     }
     
     public String toString()
     {
       return "NONEXISTENT_BUNDLE";
     }
   };
   private final Stack<RichProperties> info = new Stack();
   private final ErrorCode code;
   
   public BusinessException(ErrorCode code)
   {
     this.code = code;
     this.info.push(new RichProperties());
     ((RichProperties)this.info.peek()).setMessage(getResource(code.getName()));
   }
   
   public BusinessException(ErrorCode code, Throwable cause)
   {
     super(cause);
     this.code = code;
     this.info.push(new RichProperties());
     ((RichProperties)this.info.peek()).setMessage(getResource(code.getName()));
   }
   
   public final BusinessException reThrow(String message)
   {
     RichProperties newInfo = new RichProperties().setMessage(message);
     this.info.push(newInfo);
     return this;
   }
   
   public String getMessage()
   {
     return getInfo().getMessage();
   }
   
   public final BusinessException setMessage(String message)
   {
     getInfo().setMessage(message);
     return this;
   }
   
   public final BusinessException put(String key, Object value)
   {
     getInfo().put(key, value);
     return this;
   }
   
   public <T extends BusinessException> T as(Class<T> clazz)
   {
     if (clazz != getClass())
     {
       String msg = String.format("clazz must be: {}", new Object[] { getClass().getName() });
       throw new IllegalArgumentException(msg);
     }
     return (T) this;
   }
   
   public final Object get(String key)
   {
     return getInfo().get(key);
   }
   
   public ErrorCode getErrorCode()
   {
     return this.code;
   }
   
   RichProperties getInfo()
   {
     return (RichProperties)this.info.peek();
   }
   
   public Stack<RichProperties> getInfoStack()
   {
     return this.info;
   }
   
   private final ResourceBundle getBundle()
   {
     ResourceBundle bundle = (ResourceBundle)bundles.get(this.code.getClass());
     if (bundle != null) {
       return bundle == NONEXISTENT_BUNDLE ? null : bundle;
     }
     String path = this.code.getClass().getName().replace('.', '/');
     try
     {
       bundle = ResourceBundle.getBundle(path);
     }
     catch (MissingResourceException localMissingResourceException) {}
     if (bundle == null)
     {
       path = this.code.getClass().getSimpleName();
       try
       {
         bundle = ResourceBundle.getBundle(path);
       }
       catch (MissingResourceException localMissingResourceException1) {}
     }
     bundles.put(this.code.getClass(), bundle == null ? NONEXISTENT_BUNDLE : bundle);
     
     return bundle;
   }
   
   private String getResource(String key)
   {
     if (StringUtils.isEmpty(key)) {
       return null;
     }
     key = key.trim();
     
     String message = null;
     ResourceBundle bundle = getBundle();
     if (bundle != null) {
       try
       {
         message = bundle.getString(key);
       }
       catch (MissingResourceException localMissingResourceException) {}
     }
     if (StringUtils.isEmpty(message)) {
       message = key.replace('.', ' ');
     }
     return message;
   }
   
   public ErrorReport getReport()
   {
     ErrorReport topReport = null;
     Throwable currentThrowable = this;
     ErrorReport currentReport = null;
     ErrorReport previousReport = null;
     while (currentThrowable != null)
     {
       currentReport = new ErrorReport(currentThrowable);
       if (topReport == null) {
         topReport = currentReport;
       }
       if (previousReport != null) {
         previousReport.setCause(currentReport);
       }
       previousReport = currentReport;
       currentThrowable = currentThrowable.getCause();
     }
     return topReport;
   }
   
   public Map<String, Object> getProps()
   {
     return getInfo().getAll();
   }
 }



 
 
 