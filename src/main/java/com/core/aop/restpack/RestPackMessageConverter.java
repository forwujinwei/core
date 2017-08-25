 package com.core.aop.restpack;
 
 import com.fasterxml.jackson.databind.ObjectMapper;
 import org.springframework.http.MediaType;
 import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
 
 public class RestPackMessageConverter
   extends MappingJackson2HttpMessageConverter
 {
   public RestPackMessageConverter(ObjectMapper objectMapper)
   {
     super(objectMapper);
   }
   
   public boolean canWrite(Class<?> clazz, MediaType mediaType)
   {
     if (RestPackAspect.isRestPack()) {
       return true;
     }
     return super.canWrite(clazz, mediaType);
   }
 }