 package com.core.aop.bean;
 
 public class Arrays
 {
   public static String[] concat(String[] array1, String[] array2)
   {
     if ((array1 == null) && (array2 == null)) {
       return null;
     }
     if (array1 == null) {
       return safeCopy(array2);
     }
     if (array2 == null) {
       return safeCopy(array1);
     }
     String[] dest = new String[array1.length + array2.length];
     System.arraycopy(array1, 0, dest, 0, array1.length);
     System.arraycopy(array2, 0, dest, array1.length, 
       array2.length);
     return dest;
   }
   
   public static String[] safeCopy(String[] source)
   {
     if (source == null) {
       return null;
     }
     String[] dest = new String[source.length];
     System.arraycopy(source, 0, dest, 0, source.length);
     return dest;
   }
 }