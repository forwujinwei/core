 package com.core.aop.bean.value;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

import com.terran4j.commons.util.value.ValueSource;
 
 public final class KeyedList<K, V>
   implements ValueSource<K, V>
 {
   private final List<K> list = new ArrayList();
   private final Map<K, V> map = new HashMap();
   
   public V get(K key)
   {
     return this.map.get(key);
   }
   
   public void clear()
   {
     this.list.clear();
     this.map.clear();
   }
   
   public boolean containsKey(K key)
   {
     return this.map.containsKey(key);
   }
   
   public int getIndex(K key)
   {
     if (key == null) {
       return -1;
     }
     return this.list.indexOf(key);
   }
   
   private void checkNotNull(K key)
   {
     if (key == null) {
       throw new NullPointerException("key is null.");
     }
   }
   
   public void add(K key, V value, int index)
   {
     checkNotNull(key);
     if (containsKey(key)) {
       throw new RuntimeException("key[" + key + "] already existed of value: " + value);
     }
     this.map.put(key, value);
     this.list.add(index, key);
   }
   
   public void addOrUpdate(K key, V value)
   {
     checkNotNull(key);
     if (containsKey(key)) {
       this.map.put(key, value);
     } else {
       add(key, value, size());
     }
   }
   
   public void add(K key, V value)
   {
     add(key, value, size());
   }
   
   public V getByKey(K key)
   {
     if (key == null) {
       return null;
     }
     return this.map.get(key);
   }
   
   public V removeByKey(K key)
   {
     if (key == null) {
       return null;
     }
     V value = this.map.remove(key);
     this.list.remove(key);
     return value;
   }
   
   public int size()
   {
     return this.list.size();
   }
   
   public V get(int index)
   {
     if ((index < 0) || (index >= this.list.size())) {
       return null;
     }
     K key = this.list.get(index);
     if (key == null) {
       return null;
     }
     return this.map.get(key);
   }
   
   public K getKey(int index)
   {
     return this.list.get(index);
   }
   
   public V remove(int index)
   {
     K key = getKey(index);
     if (key != null)
     {
       this.list.remove(index);
       return this.map.remove(key);
     }
     return null;
   }
   
   public Set<K> keySet()
   {
     return this.map.keySet();
   }
   
   public List<V> getAll()
   {
     List<V> all = new ArrayList();
     for (K key : this.list)
     {
       V item = this.map.get(key);
       all.add(item);
     }
     return all;
   }
   
   public KeyedList<K, V> clone()
   {
     KeyedList<K, V> copy = new KeyedList();
     for (K key : this.list)
     {
       copy.list.add(key);
       copy.map.put(key, this.map.get(key));
     }
     return copy;
   }
   
   public KeyedList<K, V> deduct(KeyedList<K, V> other)
   {
     if ((other == null) || (other.size() == 0)) {
       return clone();
     }
     KeyedList<K, V> copy = new KeyedList();
     for (K key : this.list) {
       if (!other.containsKey(key)) {
         copy.add(key, getByKey(key));
       }
     }
     return copy;
   }
   
   public String toString()
   {
     StringBuffer sb = new StringBuffer();
     for (K key : this.list) {
       sb.append(key).append(" = ").append(this.map.get(key)).append("\n");
     }
     return sb.toString();
   }
 }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.value.KeyedList
 * JD-Core Version:    0.7.0.1
 */