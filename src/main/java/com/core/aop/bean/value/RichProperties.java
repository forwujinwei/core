package com.core.aop.bean.value;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.core.aop.bean.Strings;
import com.terran4j.commons.util.value.ValueSource;

public final class RichProperties implements ValueSource<String, String>
{
  private static final Object NULL = new Object();
  private final Map<String, Object> props = new ConcurrentHashMap();
  private String message;
  
  public RichProperties setMessage(String message)
  {
    this.message = message;
    return this;
  }
  
  public RichProperties() {}
  
  public RichProperties(String message)
  {
    this.message = message;
  }
  
  public final String getMessage()
  {
    return Strings.format(this.message, this);
  }
  
  public void put(String key, Object value)
  {
    if (value == null) {
      value = NULL;
    }
    this.props.put(key, value);
  }
  
  public Object getObject(String key)
  {
    Object value = this.props.get(key);
    if (value == NULL) {
      return null;
    }
    return value;
  }
  
  public int size()
  {
    return this.props.size();
  }
  
  public Iterator<String> iterator()
  {
    return this.props.keySet().iterator();
  }
  
  public String get(String key)
  {
    Object value = this.props.get(key);
    return Objects.toString(value);
  }
  
  public Map<String, Object> getAll()
  {
    return this.props;
  }
}
