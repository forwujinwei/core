 package com.core.aop.bean.error;
 
 public enum CommonErrorCode implements ErrorCode{
   UNKNOWN_ERROR(1, "unknown.error"),  
   INVALID_PARAM(2, "invalid.param", "key, value"),  
   NULL_PARAM(3, "null.param", "key"),  
   CONFIG_ERROR(4, "config.error"),  
   XML_ERROR(5, "xml.error"),  
   RESOURCE_NOT_FOUND(6, "resource.not.found", "type, keyName, keyValue"),  
   JSON_ERROR(7, "json.error", "jsonText"),  
   PARAM_LENGTH_TOO_LONG(8, "param.length.too.long", "key, maxLength, acutalLength"),  
   DUPLICATE_KEY(9, "duplicate.key", "key, value"),  
   INTERNAL_ERROR(10, "internal.error");
   public static final String KEY = "key";
   public static final String VALUE = "value";
   public static final String MAX_LENGTH = "maxLength";
   public static final String ACUTAL_LENGTH = "acutalLength";
   private final int value;
   private final String name;
   private final String[] requiredFields;
   
   private CommonErrorCode(int value, String name)
   {
     this.value = value;
     this.name = name;
     this.requiredFields = new String[] { "" };
   }
   
   private CommonErrorCode(int value, String name, String[] requiredFields)
   {
     this.value = value;
     this.name = name;
     this.requiredFields = requiredFields;
   }
   
   private CommonErrorCode(int value, String name, String requiredFields)
   {
     this.value = value;
     this.name = name;
     this.requiredFields = ErrorCode.toArray(requiredFields);
   }
   
   public final int getValue()
   {
     return this.value;
   }
   
   public final String getName()
   {
     return this.name;
   }
   
   public final String[] getRequiredFields()
   {
     return this.requiredFields;
   }
 }