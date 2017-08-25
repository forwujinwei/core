 package com.core.aop.bean.error;
 
 public class SimpleErrorCode
   implements ErrorCode
 {
   public static final ErrorCode UNKNOW = new SimpleErrorCode(0, "unknow.error");
   private final int value;
   private final String name;
   
   public SimpleErrorCode(int value, String name)
   {
     this.value = value;
     this.name = name;
   }
   
   public int getValue()
   {
     return this.value;
   }
   
   public String getName()
   {
     return this.name;
   }
 }
