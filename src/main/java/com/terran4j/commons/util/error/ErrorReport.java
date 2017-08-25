package com.terran4j.commons.util.error;
 
import java.util.Iterator;
import java.util.Stack;

import com.core.aop.bean.Strings;
import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.ErrorCode;
import com.core.aop.bean.value.RichProperties;
 
 public class ErrorReport
 {
   private static final String COLON = ": ";
   private static final String INDENT = "    ";
   private static final String N = "\n";
   private final Throwable throwable;
   private final String className;
   private final String methodName;
   private final String fileName;
   private final int lineNumber;
   private final ErrorCode code;
   private final Stack<RichProperties> info;
   private ErrorReport cause;
   
   public ErrorReport(Throwable throwable)
   {
     this.throwable = throwable;
     
     StackTraceElement element = throwable.getStackTrace()[0];
     this.className = element.getClassName();
     this.methodName = element.getMethodName();
     this.fileName = element.getFileName();
     this.lineNumber = element.getLineNumber();
     if ((throwable instanceof BusinessException))
     {
       BusinessException business = (BusinessException)throwable;
       this.code = business.getErrorCode();
       this.info = business.getInfoStack();
     }
     else
     {
       this.code = null;
       this.info = null;
     }
   }
   
   public Throwable getThrowable()
   {
     return this.throwable;
   }
   
   public ErrorReport getCause()
   {
     return this.cause;
   }
   
   public void setCause(ErrorReport cause)
   {
     this.cause = cause;
   }
   
   public String getClassName()
   {
     return this.className;
   }
   
   public String getMethodName()
   {
     return this.methodName;
   }
   
   public String getFileName()
   {
     return this.fileName;
   }
   
   public int getLineNumber()
   {
     return this.lineNumber;
   }
   
   public ErrorCode getCode()
   {
     return this.code;
   }
   
   public RichProperties getInfo()
   {
     if (this.info == null) {
       return null;
     }
     return (RichProperties)this.info.peek();
   }
   
   public String toString()
   {
     StringBuilder sb = new StringBuilder();
     toString(sb, this);
     return sb.toString();
   }
   
   private void toString(StringBuilder sb, RichProperties info, boolean hasMessage)
   {
     if (hasMessage) {
       sb.append(info.getMessage()).append("\n");
     }
     Iterator<String> keys = info.iterator();
     while (keys.hasNext())
     {
       String key = (String)keys.next();
       Object value = info.get(key);
       sb.append("    ").append(key).append(": ");
       String valueText = Strings.toString(value);
       sb.append(valueText).append("\n");
     }
   }
   
   private void toString(StringBuilder sb, ErrorReport report)
   {
     sb.append(report.className).append(".").append(report.methodName).append("(").append(report.fileName).append(": ").append(report.lineNumber).append(")").append("\n");
     
 
     sb.append("Error Description").append(": ").append(report.getThrowable().getMessage()).append("\n");
     
 
     ErrorCode code = report.code;
     if (code != null) {
       sb.append("    ").append("errorCode").append(": ").append(code.getValue()).append("\n").append("    ").append("errorName").append(": ").append(code.getName()).append("\n");
     }
     RichProperties topInfo = report.getInfo();
     if ((topInfo != null) && (topInfo.size() > 0)) {
       toString(sb, topInfo, false);
     }
     Stack<RichProperties> infoStack = report.info;
     if ((infoStack != null) && (infoStack.size() > 1)) {
       for (int i = infoStack.size() - 2; i >= 0; i--)
       {
         RichProperties info = (RichProperties)infoStack.get(i);
         sb.append("cause by").append(": ");
         toString(sb, info, true);
       }
     }
     Throwable throwable = report.throwable;
     if ((!(throwable instanceof BusinessException)) || (report.cause == null)) {
       sb.append(Strings.getString(throwable)).append("\n");
     }
     ErrorReport cause = report.cause;
     if (cause != null)
     {
       sb.append("cause by").append(": ");
       toString(sb, cause);
     }
   }
 }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.error.ErrorReport
 * JD-Core Version:    0.7.0.1
 */