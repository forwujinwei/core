 package com.core.aop.bean;
 
 import com.google.common.base.Joiner;
 import java.io.IOException;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.Stack;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.aop.TargetClassAware;
 import org.springframework.core.io.Resource;
 import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
 import org.springframework.core.io.support.ResourcePatternResolver;
 import org.springframework.core.type.ClassMetadata;
 import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
 import org.springframework.core.type.classreading.MetadataReader;
 import org.springframework.core.type.classreading.MetadataReaderFactory;
 import org.springframework.core.type.filter.AnnotationTypeFilter;
 import org.springframework.core.type.filter.TypeFilter;
 import org.springframework.util.Assert;
 import org.springframework.util.ClassUtils;
 
 public class Classes
 {
   private static final Logger log = LoggerFactory.getLogger(Classes.class);
   private static final Map<String, Class<?>> baseTypes = new HashMap();
   
   static
   {
     baseTypes.put("boolean", Boolean.class);
     baseTypes.put("byte", Byte.class);
     baseTypes.put("char", Character.class);
     baseTypes.put("short", Short.class);
     baseTypes.put("int", Integer.class);
     baseTypes.put("long", Long.class);
     baseTypes.put("float", Float.class);
     baseTypes.put("double", Double.class);
   }
   
   public static Class<?> toWrapType(Class<?> clazz)
   {
     if (clazz == null) {
       return null;
     }
     if (clazz.isPrimitive()) {
       return (Class)baseTypes.get(clazz.getName());
     }
     return clazz;
   }
   
   private static boolean isMatched(Class<?>[] paramClasses, Object[] paramObjects)
   {
     if (paramClasses == null) {
       paramClasses = new Class[0];
     }
     if (paramObjects == null) {
       paramObjects = new Object[0];
     }
     if (paramClasses.length != paramObjects.length) {
       return false;
     }
     for (int i = 0; i < paramObjects.length; i++)
     {
       Object arg = paramObjects[i];
       if (arg != null)
       {
         Class<?> argClass = arg.getClass();
         if ((!equals(argClass, paramClasses[i])) && (!isSuperClass(argClass, paramClasses[i]))) {
           return false;
         }
       }
     }
     return true;
   }
   
   public static boolean equals(Class<?> a, Class<?> b)
   {
     if ((a == null) && (b == null)) {
       return true;
     }
     if ((a == null) || (b == null)) {
       return false;
     }
     a = toWrapType(a);
     b = toWrapType(b);
     return a == b;
   }
   
   public static Method getMethod(Class<?> clazz, String methodName, Object[] args, Class<? extends Annotation> annoClass)
   {
     Method[] methods = clazz.getMethods();
     for (Method method : methods) {
       if (method.getName().equals(methodName))
       {
         Class[] paramClasses = method.getParameterTypes();
         if (isMatched(paramClasses, args)) {
           if ((annoClass == null) || (method.getAnnotation(annoClass) != null)) {
             return method;
           }
         }
       }
     }
     return null;
   }
   
   public static boolean isSuperClass(Class<?> child, Class<?> parent)
   {
     if ((child == null) || (parent == null)) {
       return false;
     }
     Class<?> currentSuperClass = child.getSuperclass();
     while (currentSuperClass != null)
     {
       if (currentSuperClass == parent) {
         return true;
       }
       currentSuperClass = currentSuperClass.getSuperclass();
     }
     return false;
   }
   
   public static boolean isInterface(Class<?> clazz, Class<?> interfaceClass)
   {
     if ((clazz == null) || (interfaceClass == null)) {
       return false;
     }
     Class[] interfaces = clazz.getInterfaces();
     if (interfaceClass != null) {
       for (Class<?> theInterfaceClass : interfaces) {
         if (theInterfaceClass == interfaceClass) {
           return true;
         }
       }
     }
     return false;
   }
   
   public static final Set<Class<?>> scanClasses(Class<?> basePackageClass, Class<? extends Annotation> annotationFilter)
     throws IOException, ClassNotFoundException
   {
     TypeFilter filter = annotationFilter == null ? null : new AnnotationTypeFilter(annotationFilter, false);
     return scanClasses(basePackageClass, true, filter, Thread.currentThread().getContextClassLoader());
   }
   
   public static final Set<Class<?>> scanClasses(Class<?> basePackageClass, boolean recursively, TypeFilter filter, ClassLoader classLoader)
     throws IOException, ClassNotFoundException
   {
     if (basePackageClass == null) {
       throw new NullPointerException("basePackageClass is null.");
     }
     Set<Class<?>> classes = new HashSet();
     
     String packageName = basePackageClass.getPackage().getName();
     String resourcePattern = recursively ? "/**/*.class" : "/*.class";
     String pattern = "classpath*:" + 
       ClassUtils.convertClassNameToResourcePath(packageName) + resourcePattern;
     ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
     MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
     
     String KEY_maxCount = "terran4j.util.maxScanClassCount";
     String maxCountText = System.getProperty("terran4j.util.maxScanClassCount", "1024").trim();
     int maxCount = 1024;
     try
     {
       maxCount = Integer.parseInt(maxCountText);
     }
     catch (NumberFormatException e)
     {
       throw new RuntimeException("Can't parse as int of java property[terran4j.util.maxScanClassCount]: " + maxCountText);
     }
     Resource[] resources = resourcePatternResolver.getResources(pattern);
     for (Resource resource : resources) {
       if (resource.isReadable())
       {
         MetadataReader reader = readerFactory.getMetadataReader(resource);
         String className = reader.getClassMetadata().getClassName();
         
         boolean matched = false;
         if (filter != null) {
           matched = filter.match(reader, readerFactory);
         } else {
           matched = true;
         }
         if (matched)
         {
           Class<?> clazz = classLoader.loadClass(className);
           classes.add(clazz);
           if (classes.size() > maxCount) {
             throw new RuntimeException("too many classes be scaned, can't more than: " + maxCount);
           }
         }
       }
     }
     if (log.isInfoEnabled()) {
       if (filter == null) {
         log.info("Found classes in package[{}]: \n{}", packageName, Joiner.on("\n").join(classes.iterator()));
       } else {
         log.info("Found classes with filter[{}] in package[{}]: \n{}", new Object[] { filter, packageName, 
           Joiner.on("\n").join(classes.iterator()) });
       }
     }
     return classes;
   }
   
   public static Class<?> getTargetClass(Object object)
   {
     Assert.notNull(object, "Object must not be null");
     Class<?> result = null;
     if ((object instanceof TargetClassAware)) {
       result = ((TargetClassAware)object).getTargetClass();
     }
     if (result == null) {
       result = ClassUtils.isCglibProxy(object) ? object.getClass().getSuperclass() : object.getClass();
     }
     return result;
   }
   
   public static Field getField(Class<? extends Annotation> annotationClass, Class<?> clazz)
   {
     if (clazz == null) {
       return null;
     }
     Field[] fields = clazz.getDeclaredFields();
     if (fields == null) {
       return null;
     }
     for (Field field : fields)
     {
       Annotation annotation = field.getAnnotation(annotationClass);
       if (annotation != null) {
         return field;
       }
     }
     return getField(annotationClass, clazz.getSuperclass());
   }
   
   public static Field[] getFields(Class<? extends Annotation> annotationClass, Class<?> clazz)
   {
     List<Field> fieldList = new ArrayList();
     
 
     Stack<Class<?>> classStack = new Stack();
     Class<?> currentClass = clazz;
     while (currentClass != null)
     {
       classStack.push(currentClass);
       currentClass = currentClass.getSuperclass();
     }
     while (!classStack.isEmpty())
     {
       currentClass = (Class)classStack.pop();
       loadFields(annotationClass, currentClass, fieldList);
     }
     return (Field[])fieldList.toArray(new Field[fieldList.size()]);
   }
   
   private static void loadFields(Class<? extends Annotation> annotationClass, Class<?> clazz, List<Field> fieldList)
   {
     Field[] fields = clazz.getDeclaredFields();
     if (fields == null) {
       return;
     }
     for (Field field : fields)
     {
       Annotation annotation = field.getAnnotation(annotationClass);
       if (annotation != null) {
         fieldList.add(field);
       }
     }
   }
   
   public static Method getMethod(Class<? extends Annotation> annotationClass, Class<?> clazz)
   {
     Method[] methods = clazz.getDeclaredMethods();
     if (methods == null) {
       return null;
     }
     for (Method method : methods)
     {
       Annotation annotation = method.getAnnotation(annotationClass);
       if (annotation != null) {
         return method;
       }
     }
     return null;
   }
   
   public static Method[] getMethods(Class<? extends Annotation> annotationClass, Class<?> clazz)
   {
     List<Method> methodList = new ArrayList();
     
     Method[] methods = clazz.getDeclaredMethods();
     if (methods == null) {
       return null;
     }
     for (Method method : methods)
     {
       Annotation annotation = method.getAnnotation(annotationClass);
       if (annotation != null) {
         methodList.add(method);
       }
     }
     return (Method[])methodList.toArray(new Method[methodList.size()]);
   }
   
   public static final String toIdentify(Method method)
   {
     StringBuffer sb = new StringBuffer();
     sb.append(method.getDeclaringClass().getName())
       .append("#").append(method.getName())
       .append("(");
     Class[] paramTypes = method.getParameterTypes();
     if ((paramTypes != null) && (paramTypes.length > 0))
     {
       String paramsText = Joiner.on(",").join(paramTypes);
       sb.append(paramsText);
     }
     sb.append(")");
     return sb.toString();
   }
   
   public static boolean equals(Method m1, Method m2)
   {
     if ((m1 == null) || (m2 == null)) {
       return false;
     }
     if (!m1.getName().equals(m2.getName())) {
       return false;
     }
     Class[] m1t = m1.getParameterTypes();
     Class[] m2t = m2.getParameterTypes();
     if ((m1t == null) && (m2t == null)) {
       return true;
     }
     if ((m1t == null) || (m2t == null) || (m1t.length != m2t.length)) {
       return false;
     }
     for (int i = 0; i < m1t.length; i++) {
       if (!m1t[i].equals(m2t[i])) {
         return false;
       }
     }
     return true;
   }
 }