package com.core.aop.bean;

import java.security.InvalidParameterException;

public class Maths
{
  public static final int limitIn(int value, Integer min, Integer max)
  {
    if ((min != null) && (max != null) && (min.intValue() > max.intValue()))
    {
      String msg = String.format("min can't larger than max, min = %d, max = %d.", new Object[] { min, max });
      throw new InvalidParameterException(msg);
    }
    if ((max != null) && (value > max.intValue())) {
      value = max.intValue();
    }
    if ((min != null) && (value < min.intValue())) {
      value = min.intValue();
    }
    return value;
  }
  
  public static final long limitIn(long value, Long min, Long max)
  {
    if ((min != null) && (max != null) && (min.longValue() > max.longValue()))
    {
      String msg = String.format("min can't larger than max, min = %d, max = %d.", new Object[] { min, max });
      throw new InvalidParameterException(msg);
    }
    if ((max != null) && (value > max.longValue())) {
      value = max.longValue();
    }
    if ((min != null) && (value < min.longValue())) {
      value = min.longValue();
    }
    return value;
  }
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.Maths
 * JD-Core Version:    0.7.0.1
 */