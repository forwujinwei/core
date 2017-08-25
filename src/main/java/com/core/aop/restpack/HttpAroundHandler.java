package com.core.aop.restpack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;

import com.terran4j.commons.restpack.HttpResult;

public abstract interface HttpAroundHandler
{
  public abstract int getPriority();
  
  public abstract HttpResult preHandle(ProceedingJoinPoint paramProceedingJoinPoint, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);
  
  public abstract HttpResult postHandle(ProceedingJoinPoint paramProceedingJoinPoint, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, HttpResult paramHttpResult);
}
