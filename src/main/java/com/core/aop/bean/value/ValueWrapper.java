 package com.core.aop.bean.value;

import com.terran4j.commons.util.value.ValueSource;

public class ValueWrapper
 {
   private final ValueSource<String, String> values;
   
   public ValueWrapper(ValueSource<String, String> values)
   {
     this.values = values;
   }
   
   public String get(String key)
   {
     return (String)this.values.get(key);
   }
   
   public int get(String key, int defaultValue)
   {
     String value = (String)this.values.get(key);
     if (value == null) {
       return defaultValue;
     }
     try
     {
       return Integer.parseInt(value);
     }
     catch (NumberFormatException e) {}
     return defaultValue;
   }
   
   public long get(String key, long defaultValue)
   {
     String value = (String)this.values.get(key);
     if (value == null) {
       return defaultValue;
     }
     try
     {
       return Long.parseLong(value);
     }
     catch (NumberFormatException e) {}
     return defaultValue;
   }
 }
