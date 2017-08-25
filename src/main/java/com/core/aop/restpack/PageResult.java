 package com.core.aop.restpack;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.springframework.util.StringUtils;

import com.core.aop.bean.Maths;
import com.core.aop.bean.error.BusinessException;
 
 public class PageResult<T>
 {
   private List<T> list;
   private Integer pageIndex;
   private Integer pageSize;
   private Long total;
   
   public final List<T> getList()
   {
     return this.list;
   }
   
   public final PageResult<T> setList(List<T> list)
   {
     this.list = list;
     return this;
   }
   
   public final Integer getPageIndex()
   {
     return this.pageIndex;
   }
   
   public final PageResult<T> setPageIndex(Integer pageIndex)
   {
     this.pageIndex = pageIndex;
     return this;
   }
   
   public final Integer getPageSize()
   {
     return this.pageSize;
   }
   
   public final PageResult<T> setPageSize(Integer pageSize)
   {
     this.pageSize = pageSize;
     return this;
   }
   
   public final Long getTotal()
   {
     return this.total;
   }
   
   public final PageResult<T> setTotal(Long total)
   {
     this.total = total;
     return this;
   }
   
   public <K> PageResult<K> convert(Convertor<T, K> convertor)
     throws BusinessException
   {
     PageResult<K> result = new PageResult();
     
     List<K> list = new ArrayList(this.list.size());
     for (T from : this.list)
     {
       K to = convertor.convertFrom(from);
       list.add(to);
     }
     result.setList(list);
     
     result.setPageIndex(this.pageIndex);
     result.setPageSize(this.pageSize);
     result.setTotal(this.total);
     
     return result;
   }
   
   public static Scope toRecordScope(int pageIndex, int pageSize)
   {
     return toRecordScope(pageIndex, pageSize, 100);
   }
   
   public static Scope toRecordScope(int pageIndex, int pageSize, int maxSize)
   {
     pageIndex = Maths.limitIn(pageIndex, Integer.valueOf(1), null);
     pageSize = Maths.limitIn(pageSize, Integer.valueOf(1), Integer.valueOf(maxSize));
     Scope scope = new Scope();
     scope.index = ((pageIndex - 1) * pageSize);
     scope.count = pageSize;
     return scope;
   }
   
   public static String asLikeContent(String fuzzy)
   {
     if (StringUtils.isEmpty(fuzzy)) {
       fuzzy = "%";
     } else {
       fuzzy = "%" + fuzzy.trim() + "%";
     }
     return fuzzy;
   }
   
   public static final class Scope
   {
     public int index;
     public int count;
   }
   
   public static abstract interface Convertor<T, K>
   {
     public abstract K convertFrom(T paramT)
       throws BusinessException;
   }
 }