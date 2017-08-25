package com.terran4j.commons.util.value;

public abstract interface ValueSource<K, V>
{
  public abstract V get(K paramK);
}