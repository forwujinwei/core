package com.core.aop.bean.config;

import java.util.Set;

import com.core.aop.bean.error.BusinessException;

public abstract interface ConfigElement
{
  public abstract String attr(String paramString);
  
  public abstract String attr(String paramString1, String paramString2);
  
  public abstract int attr(String paramString, int paramInt);
  
  public abstract Integer attrAsInt(String paramString);
  
  public abstract ConfigElement[] getChildren();
  
  public abstract ConfigElement[] getChildren(String paramString);
  
  public abstract String getValue();
  
  public abstract boolean attr(String paramString, boolean paramBoolean);
  
  public abstract Boolean attrAsBoolean(String paramString);
  
  public abstract <T> T attr(String paramString, Class<T> paramClass)
    throws BusinessException;
  
  public abstract ConfigElement getChild(String paramString)
    throws BusinessException;
  
  public abstract String getChildText(String paramString)
    throws BusinessException;
  
  public abstract String getName();
  
  public abstract Set<String> attrSet();
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.config.ConfigElement
 * JD-Core Version:    0.7.0.1
 */