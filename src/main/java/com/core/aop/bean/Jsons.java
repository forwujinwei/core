 package com.core.aop.bean;
 
 import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

import com.core.aop.bean.config.ConfigElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
 
 public class Jsons
 {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final JsonParser parser = new JsonParser();
   private static final ObjectMapper objectMapper = createObjectMapper();
   
   public static final ObjectMapper createObjectMapper()
   {
     ObjectMapper objectMapper = new ObjectMapper();
     
     objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
     
 
     objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    
 
     objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
     
     objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
     objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
     
 
     objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
     
 
     objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
     
 
     objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
     
     return objectMapper;
   }
   
   public static final ObjectMapper getObjectMapper()
   {
     return objectMapper;
   }
   
   public static final JsonElement parseJson(String jsonText)
   {
     return parser.parse(jsonText);
   }
   
   public static final JsonElement toJson(ConfigElement element)
   {
     if (element == null) {
       return null;
     }
     JsonObject json = new JsonObject();
     
     json.add("tag", new JsonPrimitive(element.getName()));
     
     String value = element.getValue();
     if (value != null) {
       json.add("value", new JsonPrimitive(value));
     }
     Set<String> attr = element.attrSet();
     String attrValue;
     if ((attr != null) && (attr.size() > 0))
     {
       JsonObject jsonAttr = new JsonObject();
       Iterator<String> it = attr.iterator();
       while (it.hasNext())
       {
         String attrKey = (String)it.next();
         attrValue = element.attr(attrKey);
         if ((attrKey != null) && (attrValue != null)) {
           jsonAttr.add(attrKey, new JsonPrimitive(attrValue));
         }
       }
       json.add("attr", jsonAttr);
     }
     ConfigElement[] children = element.getChildren();
     if ((children != null) && (children.length > 0))
     {
       JsonArray jsonChildren = new JsonArray();
       ConfigElement[] arrayOfConfigElement1;
       String str1 = (arrayOfConfigElement1 = children).length;
       for (attrValue = 0; attrValue < str1; attrValue++)
       {
         ConfigElement child = arrayOfConfigElement1[attrValue];
         JsonElement jsonChild = toJson(child);
         if (jsonChild != null) {
          jsonChildren.add(jsonChild);
        }
      }
      json.add("children", jsonChildren);
    }
    return json;
  }
  
  public static String format(JsonElement json)
  {
    String prettyJsonText = gson.toJson(json);
    return prettyJsonText;
  }
  
  public static String format(String uglyJsonText)
  {
    JsonElement je = parser.parse(uglyJsonText);
    String prettyJsonText = format(je);
    return prettyJsonText;
  }
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.Jsons
 * JD-Core Version:    0.7.0.1
 */