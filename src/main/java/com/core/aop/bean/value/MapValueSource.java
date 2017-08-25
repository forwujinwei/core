package com.core.aop.bean.value;

import java.util.HashMap;
import java.util.Map;

import com.terran4j.commons.util.value.ValueSource;

public class MapValueSource<K, V>
  implements ValueSource<K, V>
{
  private final Map<K, V> map;
  
  public MapValueSource()
  {
    this(new HashMap());
  }
  
  public MapValueSource(Map<K, V> map)
  {
    this.map = map;
  }
  
  public MapValueSource<K, V> put(K key, V value)
  {
    this.map.put(key, value);
    return this;
  }
  
  public V get(K key)
  {
    return this.map.get(key);
  }
}