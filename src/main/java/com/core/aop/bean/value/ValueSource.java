package com.core.aop.bean.value;

public abstract interface ValueSource<K, V>{
  public abstract V get(K paramK);
}
