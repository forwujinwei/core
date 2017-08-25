package com.core.aop.restpack;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableWebMvc
@Import({RestPackConfiguration.class})
public @interface EnableRestPack {}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-restpack\1.0.8\terran4j-commons-restpack-1.0.8.jar
 * Qualified Name:     com.terran4j.commons.restpack.EnableRestPack
 * JD-Core Version:    0.7.0.1
 */