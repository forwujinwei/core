 package com.terran4j.commons.restpack;
 
 import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.core.aop.bean.Strings;
import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
 
 @JsonInclude(JsonInclude.Include.NON_NULL)
 public class HttpResult
 {
   public static final String KEY_resultCode = "resultCode";
   public static final String KEY_data = "data";
   public static final String KEY_message = "message";
   public static final int SUCCESS_CODE = 0;
   private String requestId;
   private long serverTime;
   private long spendTime;
   private int resultCode;
   private Object data;
   private String message;
   private Map<String, Object> props = null;
   
   public static HttpResult success()
   {
     HttpResult response = new HttpResult();
     response.setResultCode(0);
     return response;
   }
   
   public static HttpResult success(Object data)
   {
     HttpResult response = new HttpResult();
     response.setResultCode(0);
     response.setData(data);
     return response;
   }
   
   public static HttpResult fail(ErrorCode errorCode, String msg)
   {
     if (errorCode == null) {
       throw new NullPointerException("errorCode is null.");
     }
     if (StringUtils.isEmpty(msg)) {
       msg = errorCode.getName().replace('.', ' ');
     }
     HttpResult response = new HttpResult();
     response.setResultCode(errorCode.getValue());
     response.setMessage(msg);
     return response;
   }
   
   public static HttpResult fail(BusinessException e)
   {
     HttpResult response = new HttpResult();
     response.setResultCode(e.getErrorCode().getValue());
     response.setMessage(e.getMessage());
     
     Map<String, Object> errProps = e.getProps();
     if ((errProps != null) && (errProps.size() > 0))
     {
       if (response.props == null) {
         response.props = new HashMap();
       }
       response.props.putAll(errProps);
     }
     return response;
   }
   
   public void clearPropsIfEmpty()
   {
     if ((this.props != null) && (this.props.size() == 0)) {
       this.props = null;
     }
   }
   
   public String getRequestId()
   {
     return this.requestId;
   }
   
   public void setRequestId(String requestId)
   {
     this.requestId = requestId;
   }
   
   public long getServerTime()
   {
     return this.serverTime;
   }
   
   public void setServerTime(long serverTime)
   {
     this.serverTime = serverTime;
   }
   
   public int getResultCode()
   {
     return this.resultCode;
   }
   
   public void setResultCode(int resultCode)
   {
     this.resultCode = resultCode;
   }
   
   public String getMessage()
   {
     return this.message;
   }
   
   public void setMessage(String message)
   {
     this.message = message;
   }
   
   public Object getData()
   {
     return this.data;
   }
   
   public void setData(Object data)
   {
     this.data = data;
   }
   
   public long getSpendTime()
   {
     return this.spendTime;
   }
   
   public void setSpendTime(long spendTime)
   {
     this.spendTime = spendTime;
   }
   
   public Map<String, Object> getProps()
   {
     return this.props;
   }
   
   public void setProps(Map<String, Object> props)
   {
     this.props = props;
   }
   
   public final String toString()
   {
     return Strings.toString(this);
   }
 }