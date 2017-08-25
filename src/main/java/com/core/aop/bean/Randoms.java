package com.core.aop.bean;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Randoms
{
  private static final char[] TOKEN_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 
    'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 
    'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', 
    '2', '3', '4', '5', '6', '7', '8', '9' };
  private static final char[] TOKEN_NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  private static SecureRandom random = null;
  
  private static SecureRandom getRandom()
  {
    if (random != null) {
      return random;
    }
    try
    {
      random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.currentTimeMillis());
      return random;
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public static final String createToken(int count)
  {
    return createToken(count, TOKEN_CHARS);
  }
  
  public static final String createNumberToken(int count)
  {
    return createToken(count, TOKEN_NUMBERS);
  }
  
  public static final String createToken(int count, char[] scope)
  {
    StringBuffer sb = new StringBuffer();
    Random random = getRandom();
    for (int i = 0; i < count; i++)
    {
      char c = scope[random.nextInt(scope.length)];
      sb.append(String.valueOf(c));
    }
    String token = sb.toString();
    return token;
  }
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.Randoms
 * JD-Core Version:    0.7.0.1
 */