package com.ewu.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortListUtil
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
               Method m1 = ((E)a).getClass().getMethod(method, new Class[]{});
               Method m2 = ((E)b).getClass().getMethod(method,  new Class[]{});
               
               if (null != sort && "desc".equals(sort))
               {
                  ret = m2.invoke(((E)b),  new Object[]{}).toString().compareTo(m1.invoke(((E)a), new Object[]{}).toString());
               }
               else
               {
                  ret = m1.invoke(((E)a),  new Object[]{}).toString().compareTo(m2.invoke(((E)b), new Object[]{}).toString());
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
