 package com.core.aop.bean.value;
 
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonPrimitive;
 
 public class JsonValueSource implements ValueSource<String, String>{
   private final JsonObject result;
   
   public JsonValueSource(JsonObject result)
   {
     this.result = result;
   }
   
   public JsonObject getSource()
   {
     return this.result;
   }
   
   public String get(String key)
   {
     if (this.result == null) {
       return null;
     }
     String[] array = key.split("\\.");
     JsonElement current = this.result;
     for (String item : array)
     {
       JsonElement element = current.getAsJsonObject().get(item);
       if (element == null) {
         return null;
       }
       current = element;
     }
     return current.getAsJsonPrimitive().getAsString();
   }
   
   public JsonElement getElement(String key)
   {
     if (this.result == null) {
       return null;
     }
     String[] array = key.split("\\.");
     JsonElement current = this.result;
     for (String item : array)
     {
       JsonElement element = current.getAsJsonObject().get(item);
       if (element == null) {
         return null;
       }
       current = element;
     }
     return current;
   }
 }