 package com.core.aop.bean.value;
 
 import java.util.Stack;

import com.terran4j.commons.util.value.ValueSource;
 
 public class ValueSources<K, V> implements ValueSource<K, V>
 {
   private Stack<ValueSource<K, V>> stack = new Stack();
   
   public V get(K key)
   {
     if (key == null) {
       return null;
     }
     if (this.stack.size() == 0) {
       return null;
     }
     for (int i = 0; i < this.stack.size(); i++)
     {
       ValueSource<K, V> values = (ValueSource)this.stack.get(i);
       V value = values.get(key);
       if (value != null) {
         return value;
       }
     }
     return null;
   }
   
   public ValueSource<K, V> push(ValueSource<K, V> item)
   {
     if (item == null) {
       throw new NullPointerException("push ValueGetter is null.");
     }
     this.stack.push(item);
     return this;
   }
   
   public ValueSource<K, V> pop()
   {
     return (ValueSource)this.stack.pop();
   }
 }