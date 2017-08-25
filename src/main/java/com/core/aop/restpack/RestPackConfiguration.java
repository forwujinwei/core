package com.core.aop.restpack;

import com.core.aop.bean.Jsons;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class RestPackConfiguration
  extends WebMvcConfigurerAdapter
{
  private final ObjectMapper objectMapper = Jsons.createObjectMapper();
  
  @Bean
  public HttpErrorHandler httpErrorHandler()
  {
    return new HttpErrorHandler();
  }
  
  @Bean
  public RestPackAspect restPackAspect()
  {
    return new RestPackAspect(this.objectMapper);
  }
  
  @Bean
  public RestPackAdvice restPackAdvice()
  {
    return new RestPackAdvice();
  }
  
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
  {
    List<HttpMessageConverter<?>> removedConverters = new ArrayList<>();
    for (HttpMessageConverter<?> converter : converters) {
      if ((converter instanceof MappingJackson2HttpMessageConverter)) {
        removedConverters.add(converter);
      }
    }
    converters.removeAll(removedConverters);
    HttpMessageConverter<?> restPackConverter = new RestPackMessageConverter(this.objectMapper);
    converters.add(restPackConverter);
  }
}