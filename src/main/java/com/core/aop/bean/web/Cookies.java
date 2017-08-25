 package com.core.aop.bean.web;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.http.Cookie;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.util.StringUtils;
 
 public class Cookies
 {
   private static final Logger log = LoggerFactory.getLogger(Cookies.class);
   public static final String EQUALS = "=";
   public static final String COOKIE_SECURE = "Secure";
   public static final String COOKIE_HTTP_ONLY = "HttpOnly";
   public static final String SET_COOKIE = "Set-Cookie";
   public static final String MAX_AGE = "Max-Age";
   public static final String COOKIE_SPLITER = "; ";
   
   public static void setCookie(HttpServletResponse response, Map<String, String> cookieValues)
   {
     setCookie(response, cookieValues, -1);
   }
   
   public static void setCookie(HttpServletResponse response, String key, String value, int maxAge)
   {
     if (StringUtils.isEmpty(key)) {
       return;
     }
     Map<String, String> cookieValues = new HashMap();
     cookieValues.put(key, value);
     setCookie(response, cookieValues, maxAge);
   }
   
   public static void setCookie(HttpServletResponse response, Map<String, String> cookieValues, int maxAge)
   {
     if (cookieValues == null) {
       throw new NullPointerException("cookieValues is null.");
     }
     Iterator<String> it = cookieValues.keySet().iterator();
     while (it.hasNext())
     {
       String key = (String)it.next();
       if (key != null)
       {
         String value = (String)cookieValues.get(key);
         if (value != null)
         {
           Cookie cookieItem = new Cookie(key, value);
           cookieItem.setPath("/");
           cookieItem.setMaxAge(maxAge);
           response.addCookie(cookieItem);
           if (log.isInfoEnabled()) {
             log.info("addCookie, key = " + key + ", value = " + value);
           }
         }
         else
         {
           Cookie cookieItem = new Cookie(key, null);
           cookieItem.setPath("/");
           cookieItem.setMaxAge(0);
           response.addCookie(cookieItem);
           if (log.isInfoEnabled()) {
             log.info("removeCookie, key = " + key);
           }
         }
       }
     }
   }
   
   public static void removeCookie(HttpServletResponse response, String cookieName)
   {
     if (StringUtils.isEmpty(cookieName)) {
       throw new NullPointerException("cookieName is empty.");
     }
     Map<String, String> removeCookie = new HashMap();
     removeCookie.put(cookieName, null);
     setCookie(response, removeCookie, 0);
   }
   
   public static String getCookie(HttpServletRequest request, String cookieName)
   {
     if (request == null) {
       throw new NullPointerException("request is null.");
     }
     if (StringUtils.isEmpty(cookieName)) {
       throw new NullPointerException("cookieName is empty.");
     }
     Cookie[] cookies = request.getCookies();
     if ((cookies == null) || (cookies.length == 0)) {
       return null;
     }
     for (Cookie c : cookies) {
       if (cookieName.equals(c.getName())) {
         return c.getValue();
       }
     }
     return null;
   }
 }