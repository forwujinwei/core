 package com.core.aop.bean;
 
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.CommonErrorCode;
import com.terran4j.commons.util.value.ValueSource;
 
 public class Beans
 {
   private static final Set<String> baseTypes = new HashSet();
   private static List<Class<?>> baseClasses;
   
   static
   {
     baseTypes.add("boolean");
     baseTypes.add("byte");
     baseTypes.add("char");
     baseTypes.add("short");
     baseTypes.add("int");
     baseTypes.add("long");
     baseTypes.add("float");
     baseTypes.add("double");
     
 
     baseClasses = new ArrayList();
     
 
     baseClasses.add(Boolean.class);
     baseClasses.add(Byte.class);
     baseClasses.add(Character.class);
     baseClasses.add(Short.class);
     baseClasses.add(Integer.class);
     baseClasses.add(Long.class);
     baseClasses.add(Float.class);
     baseClasses.add(Double.class);
     baseClasses.add(String.class);
   }
   
   private static Object toBaseObject(Class<?> clazz, String value)
   {
     if (value == null) {
       return null;
     }
     Object paramObj = null;
     if ((clazz.equals(Integer.class)) || (clazz.getName() == "int")) {
       paramObj = Integer.valueOf(value);
     } else if ((clazz.equals(Double.class)) || (clazz.getName() == "double")) {
       paramObj = Double.valueOf(value);
     } else if ((clazz.equals(Long.class)) || (clazz.getName() == "long")) {
       paramObj = Long.valueOf(value);
     } else if ((clazz.equals(Boolean.class)) || (clazz.getName() == "boolean")) {
       paramObj = Boolean.valueOf(value);
     } else {
       paramObj = value;
     }
     return paramObj;
   }
   
   private static String upcaseHeadLetter(String str)
   {
     return str.substring(0, 1).toUpperCase() + str.substring(1);
   }
   
   private static boolean contains(int value, int flag)
   {
     return (value & flag) == flag;
   }
   
   private static boolean isUpperLetter(char c)
   {
     return (c >= 'A') && (c <= 'Z');
   }
   
   public static <T> T from(ValueSource<String, Object> values, Class<T> clazz)
   {
     try
     {
       T bean = clazz.newInstance();
       if (values == null) {
         return bean;
       }
       Map<String, Method> setMethods = getAllSetMethods(clazz);
       if ((setMethods == null) || (setMethods.size() == 0)) {
         return bean;
       }
       Iterator<String> it = setMethods.keySet().iterator();
       while (it.hasNext())
       {
         String fieldName = (String)it.next();
         Method setMethod = (Method)setMethods.get(fieldName);
         Object value = values.get(fieldName);
         if (value != null) {
           try
           {
             Class<?> parameterClass = setMethod.getParameterTypes()[0];
             Object paramObj = toBaseObject(parameterClass, value.toString());
             setMethod.invoke(bean, new Object[] { paramObj });
           }
           catch (SecurityException|IllegalArgumentException|InvocationTargetException e)
           {
             String msg = String.format("set field[{}] of class[{}] to value[{}] failed: {}", new Object[] {
               fieldName, clazz.getName(), value, e.getMessage() });
             throw new RuntimeException(msg, e);
           }
         }
       }
       return bean;
     }
     catch (InstantiationException|IllegalAccessException e)
     {
       String msg = String.format("create object[{}] from values[{}] failed: {}", new Object[] {
         clazz.getName(), values, e.getMessage() });
       throw new RuntimeException(msg, e);
     }
   }
   
   public static <T> T from(Properties props, Class<T> clazz)
   {
     try
     {
       T bean = clazz.newInstance();
       if (props == null) {
         return bean;
       }
       for (Object keyObj : props.keySet()) {
         if ((keyObj instanceof String))
         {
           String fieldName = (String)keyObj;
           
           Object value = props.get(fieldName);
           if (value != null)
           {
             Method setMethod = getSetMethod(clazz, fieldName);
             if (setMethod != null) {
               try
               {
                 Class<?> parameterClass = setMethod.getParameterTypes()[0];
                 Object paramObj = toBaseObject(parameterClass, value.toString());
                 setMethod.invoke(bean, new Object[] { paramObj });
               }
               catch (SecurityException|IllegalArgumentException|InvocationTargetException e)
               {
                 String msg = String.format("set field[{}] of class[{}] to value[{}] failed: {}", new Object[] {
                   fieldName, clazz.getName(), value, e.getMessage() });
                 throw new RuntimeException(msg, e);
               }
             }
           }
         }
       }
       return bean;
     }
     catch (InstantiationException|IllegalAccessException e)
     {
       String msg = String.format("create object[{}] from properties[{}] failed: {}", new Object[] {
         clazz.getName(), props, e.getMessage() });
       throw new RuntimeException(msg, e);
     }
   }
   
   public static <T> T getFieldValue(Object bean, String fieldName, Class<?> filedClass)
   {
     if (bean == null) {
       throw new NullPointerException("bean is null.");
     }
     if (fieldName == null) {
       throw new NullPointerException("fieldName is null.");
     }
     Class<?> clazz = bean.getClass();
     Field field = null;
     try
     {
       field = clazz.getDeclaredField(fieldName);
     }
     catch (SecurityException e)
     {
       throw new RuntimeException(e);
     }
     catch (NoSuchFieldException e)
     {
       return null;
     }
     if (field == null) {
       return null;
     }
     Object fieldValue = null;
     try
     {
       fieldValue = field.get(bean);
     }
     catch (IllegalArgumentException|IllegalAccessException e)
     {
       throw new RuntimeException(e);
     }
     if (fieldValue == null) {
       return null;
     }
     if (!filedClass.isInstance(fieldValue)) {
       throw new ClassCastException("field[" + fieldName + "] is not the Class: " + filedClass);
     }
     return (T) fieldValue;
   }
   
   public static Map<String, Method> getAllSetMethods(Class<?> clazz)
   {
     if (clazz == null) {
       throw new NullPointerException("clazz is null.");
     }
     Map<String, Method> result = new HashMap();
     Method[] methods = clazz.getMethods();
     for (Method method : methods)
     {
       String fieldName = getFieldNameBySetMethod(method);
       if (fieldName != null) {
         result.put(fieldName, method);
       }
     }
     return result;
   }
   
   public static String getFieldNameBySetMethod(Method method)
   {
     if (method == null) {
       return null;
     }
     int modifiers = method.getModifiers();
     if (!contains(modifiers, 1)) {
       return null;
     }
     if (contains(modifiers, 8)) {
       return null;
     }
     if (method.isSynthetic()) {
       return null;
     }
     Class[] parameterTypes = method.getParameterTypes();
     if ((parameterTypes == null) || (parameterTypes.length != 1)) {
       return null;
     }
     Class<?> parameterType = parameterTypes[0];
     if (!isBasicType(parameterType)) {
       return null;
     }
     boolean isBooleanType = (parameterType.equals(Boolean.class)) || (parameterType.getName() == "boolean");
     
     String name = method.getName();
     String fieldName = null;
     if ((name.startsWith("is")) && (isBooleanType))
     {
       if ((name.length() < 3) || (!isUpperLetter(name.charAt(2)))) {
         return null;
       }
       fieldName = name.substring(2, 3).toLowerCase() + name.substring(3);
     }
     else if ((name.startsWith("set")) && (!isBooleanType))
     {
       if ((name.length() < 4) || (!isUpperLetter(name.charAt(3)))) {
         return null;
       }
       fieldName = name.substring(3, 4).toLowerCase() + name.substring(4);
     }
     return fieldName;
   }
   
   public static Method getSetMethod(Class<?> clazz, String fieldName)
   {
     String setMethodName = getSetMethodName(clazz, fieldName);
     Method[] methods = clazz.getMethods();
     for (Method method : methods) {
       if (method.getName().equals(setMethodName)) {
         return method;
       }
     }
     return null;
   }
   
   public static Method getGetMethod(Class<?> clazz, String fieldName)
   {
     String getMethodName = getGetMethodName(clazz, fieldName);
     Method[] methods = clazz.getMethods();
     for (Method method : methods) {
       if (method.getName().equals(getMethodName)) {
         return method;
       }
     }
     return null;
   }
   
   public static String getSetMethodName(Class<?> clazz, String fieldName)
   {
     if (isBooleanField(clazz, fieldName)) {
       if ((fieldName.startsWith("is")) && (fieldName.length() > 2))
       {
         String fieldName2 = fieldName.substring(2);
         return "set" + upcaseHeadLetter(fieldName2);
       }
     }
     return "set" + upcaseHeadLetter(fieldName);
   }
   
   private static boolean isBooleanField(Class<?> clazz, String fieldName)
   {
     Field field = null;
     try
     {
       field = clazz.getDeclaredField(fieldName);
     }
     catch (SecurityException e)
     {
       e.printStackTrace();
     }
     catch (NoSuchFieldException e)
     {
       e.printStackTrace();
     }
     if (field == null) {
       return false;
     }
     String fieldClassName = field.getType().getName();
     if (("boolean".equals(fieldClassName)) || (Boolean.class.getName().equals(fieldClassName))) {
       return true;
     }
     return false;
   }
   
   public static String getGetMethodName(Class<?> clazz, String fieldName)
   {
     if (isBooleanField(clazz, fieldName))
     {
       if ((fieldName.startsWith("is")) && (fieldName.length() > 2)) {
         return fieldName;
       }
       return "is" + upcaseHeadLetter(fieldName);
     }
     return "get" + upcaseHeadLetter(fieldName);
   }
   
   public static boolean isBasicType(Class<?> clazz)
   {
     if (clazz == null) {
       return false;
     }
     if (baseClasses.contains(clazz)) {
       return true;
     }
     return baseTypes.contains(clazz.getName());
   }
   
   public static void copy(Object dest, Object orig)
     throws BusinessException
   {
     if (orig == null) {
       return;
     }
     if (dest == null) {
       throw new NullPointerException("dest is null.");
     }
     try
     {
       BeanUtils.copyProperties(dest, orig);
     }
     catch (IllegalAccessException|InvocationTargetException e)
     {
       throw 
         new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("copy orig bean to dest bean error.");
     }
   }
   
   public static <T> T createBy(Class<T> destClass, Object orig)
     throws BusinessException
   {
     try
     {
       T dest = destClass.newInstance();
       copy(dest, orig);
       return dest;
     }
     catch (InstantiationException|IllegalAccessException e)
     {
       throw 
         new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("create a new bean by orig bean error.");
     }
   }
 }