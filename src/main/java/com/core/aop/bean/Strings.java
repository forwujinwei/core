package com.core.aop.bean;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.terran4j.commons.util.value.ValueSource;

public class Strings
{
  private static final Logger log = LoggerFactory.getLogger(Strings.class);
  private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static final String toString(Object value)
  {
    if (value == null) {
      return "";
    }
    try
    {
      return Jsons.getObjectMapper().writeValueAsString(value);
    }
    catch (JsonProcessingException e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  public static String getString(Throwable t)
  {
    if (t == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try
    {
      PrintWriter pw = new PrintWriter(out);
      t.printStackTrace(pw);
      pw.flush();
      String str = new String(out.toByteArray(), Encoding.UTF8.getName());
      return str;
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    finally
    {
      try
      {
        out.close();
      }
      catch (Exception localException1) {}
    }
  }
  
  public static String getString(Class<?> clazz, String fileName)
  {
    String path = null;
    ClassLoader loader = null;
    if (clazz == null)
    {
      path = fileName;
      loader = Strings.class.getClassLoader();
    }
    else
    {
      path = getClassPath(clazz, fileName);
      loader = clazz.getClassLoader();
    }
    InputStream in = loader.getResourceAsStream(path);
    if (in == null) {
      return null;
    }
    try
    {
      String str = getString(in);
      if (str == null) {
        str = "";
      }
      return str;
    }
    finally
    {
      if (in != null) {
        try
        {
          in.close();
        }
        catch (IOException localIOException1) {}
      }
    }
  }
  
  public static InputStream toInputStream(String str)
  {
    if (str == null) {
      return null;
    }
    try
    {
      return new ByteArrayInputStream(str.getBytes(Encoding.UTF8.getName()));
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String getString(InputStream in)
  {
    return getString(in, Encoding.getDefaultEncoding());
  }
  
  public static String getString(InputStream in, Encoding encoding)
  {
    StringBuilder sb = new StringBuilder();
    try
    {
      if (encoding == null) {
        encoding = Encoding.getDefaultEncoding();
      }
      InputStreamReader inr = new InputStreamReader(in, encoding.getName());
      BufferedReader reader = new BufferedReader(inr);
      
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return sb.toString();
  }
  
  public static String getClassPath(Class<?> clazz, String fileName)
  {
    Package classPackage = clazz.getPackage();
    if (classPackage != null) {
      return classPackage.getName().replace('.', '/') + "/" + fileName;
    }
    return fileName;
  }
  
  public static String format(String str, Map<String, Object> args, final boolean nullAsEmpty)
  {
    ValueSource<String, String> values = new ValueSource(){
      public String get(String key)
      {
        Object value = null;
        if (Strings.this != null) {
          value = Strings.this.get(key);
        }
        if ((nullAsEmpty) && (value == null)) {
          value = "";
        }
        return Objects.toString(value);
      }
    };
    return format(str, values, "${", "}", null);
  }
  
  public static String format(String str, Map<String, Object> args)
  {
    return format(str, args, false);
  }
  
  public static String format(String str, ValueSource<String, String> values)
  {
    return format(str, values, "${", "}", null);
  }
  
  public static String format(String str, ValueSource<String, String> values, String begin, String end, List<String> notMatched)
  {
    if ((str == null) || (str.trim().length() == 0)) {
      return str;
    }
    if ((begin == null) || (begin.trim().length() == 0) || (end == null) || (end.trim().length() == 0)) {
      throw new NullPointerException("begin or end is null or empty：" + begin + ", " + end);
    }
    StringBuffer sb = new StringBuffer();
    int size = str.length();
    int beginLength = begin.length();
    int endLength = end.length();
    int from = 0;
    for (;;)
    {
      int m = str.indexOf(begin, from);
      int n = str.indexOf(end, from);
      if ((m < 0) || (m >= size) || (n <= m) || (n >= size)) {
        break;
      }
      String s0 = str.substring(from, m);
      sb.append(s0);
      String sMatch = str.substring(m, n + endLength);
      String key = sMatch.substring(beginLength, sMatch.length() - endLength);
      String value = (String)values.get(key);
      if (value != null)
      {
        sb.append(value);
      }
      else
      {
        sb.append(sMatch);
        if (notMatched != null) {
          notMatched.add(key);
        }
      }
      from = n + endLength;
    }
    if (from < size) {
      sb.append(str.substring(from));
    }
    return sb.toString();
  }
  
  public static String[] splitWithTrim(String content)
  {
    return splitWithTrim(content, -1);
  }
  
  public static String[] splitWithTrim(String content, String regex)
  {
    return splitWithTrim(content, regex, -1);
  }
  
  public static String[] splitWithTrim(String content, int limit)
  {
    if (content == null) {
      return null;
    }
    return splitWithTrim(content, " ", limit);
  }
  
  public static String[] splitWithTrim(String content, String regex, int limit)
  {
    if (content == null) {
      return null;
    }
    List<String> result = new ArrayList();
    String[] strs = content.split(regex, limit);
    if (strs != null) {
      for (String str : strs)
      {
        str = str.trim();
        if (!StringUtils.isEmpty(str)) {
          result.add(str);
        }
      }
    }
    if ((limit >= 0) && (result.size() < limit))
    {
      int leftLimit = limit - result.size();
      int lastIndex = result.size() - 1;
      String lastContent = (String)result.get(lastIndex);
      String[] leftStrs = splitWithTrim(lastContent, regex, leftLimit);
      if ((leftStrs != null) && (leftStrs.length > 0))
      {
        result.remove(lastIndex);
        result.addAll(Arrays.asList(leftStrs));
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  
  public static String toHexString(byte[] data)
  {
    if ((data == null) || (data.length == 0)) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
    {
      sb.append(HEX_CHAR[((data[i] & 0xF0) >>> 4)]);
      
      sb.append(HEX_CHAR[(data[i] & 0xF)]);
    }
    return sb.toString();
  }
  
  public static byte[] fromHexString(String str)
  {
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    str = str.trim().toLowerCase();
    if (str.length() % 2 == 1) {
      throw new InvalidParameterException("无效的16进制字符串：" + str);
    }
    byte[] data = new byte[str.length() / 2];
    
    int n = 0;
    for (int i = 0; i < str.length() - 1; i += 2)
    {
      int lowValue = hexToNumber(i + 1, str);
      int highValue = hexToNumber(i, str);
      int value = highValue * 16 + lowValue;
      data[n] = ((byte)value);n++;
    }
    return data;
  }
  
  private static final int hexToNumber(int i, String str)
  {
    char c = str.charAt(i);
    int value = c >= 'a' ? c - 'a' + 10 : c - '0';
    if ((value < 0) || (value > HEX_CHAR.length - 1))
    {
      String msg = String.format("无效的16进制字符串：%s, 第%d个字符不合法: %s", new Object[] { str, Integer.valueOf(i + 1), Character.valueOf(c) });
      throw new InvalidParameterException(msg);
    }
    return value;
  }
  
  public static final String toString(byte[] bytes)
  {
    if (bytes == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int i = 0; i < bytes.length; i++)
    {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(bytes[i]);
    }
    sb.append("]");
    return sb.toString();
  }
  
  public static boolean match(String path, String... regexPaths)
  {
    PathMatcher pathMatch = new AntPathMatcher();
    if ((path == null) || (regexPaths == null)) {
      return false;
    }
    for (String regexPath : regexPaths) {
      if (regexPath != null)
      {
        regexPath = regexPath.trim();
        path = path.trim();
        if (pathMatch.match(regexPath, path)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static Map<String, String> toMap(String content, String split, String joiner)
  {
    String[] array = splitWithTrim(content, split);
    if ((array == null) || (array.length == 0)) {
      return null;
    }
    Map<String, String> result = new HashMap();
    for (String item : array) {
      if ((item != null) && (item.trim().length() != 0))
      {
        int i = item.indexOf(joiner);
        if (((i <= 0) || (i > item.length() - 1)) && 
          (log.isWarnEnabled())) {
          log.warn("unresolve[" + item + "] " + "for experssion: " + item);
        }
        String key = item.substring(0, i).trim();
        String value = item.substring(i + joiner.length()).trim();
        result.put(key, value);
      }
    }
    return result;
  }
}
 