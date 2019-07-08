package com.ewu.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortListObjectUtil
{
   public static final String ORDER_BY_ASC = "asc";
   public static final String ORDER_BY_DESC = "desc";

   public static <E> void sort(List<E> list, final String method, final String sort)
   {
      Collections.sort(list, new Comparator()
      {

         @Override
         public int compare(Object a, Object b)
         {
            int ret = 0;
            try
            {
               Method m1 = ((E)a).getClass().getMethod(method, null);
               Method m2 = ((E)b).getClass().getMethod(method, null);
               
               if (null != sort && "desc".equals(sort))
               {
                  ret = m2.invoke(((E)b), null).toString().compareTo(m1.invoke(((E)a), null).toString());
               }
               else
               {
                  ret = m1.invoke(((E)a), null).toString().compareTo(m2.invoke(((E)b), null).toString()); 
               }
            }
            catch (NoSuchMethodException e)
            {
               e.printStackTrace();
            }
            catch (IllegalAccessException e) 
            {
               e.printStackTrace();
            }
            catch (InvocationTargetException e) 
            {
               e.printStackTrace();
            }
            
            return ret;
         }});
   }
}
