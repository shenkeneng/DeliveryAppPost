/**
 * <p>
 * Copyright: Copyright (c) 2014
 * Company: ZTE
 * Description: ����д����ļ��Ǹ�ʲô�õ�
 * </p>
 * @Title SharedPreferencesHelper.java
 * @Package com.hbcloud.haojihui.utils
 * @version 1.0
 * @author wsy
 * @date 2014-10-29
 */
package com.ewu.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper
{
    private static SharedPreferencesHelper instance;
    
    private SharedPreferences sharePref;
    
    private SharedPreferences.Editor editor;

    public static SharedPreferencesHelper getInstance(Context context, String name)
    {
       if (instance == null)
       {
          instance = new SharedPreferencesHelper(context, name);
       }
       
       return instance;
    }
    
    private SharedPreferencesHelper(Context context, String name)
    {
       sharePref = context.getSharedPreferences(name, 0);
       editor = sharePref.edit();
    }
    
    public void putValue(String key, String value)
    {
       if (null == editor)
       {
          editor = sharePref.edit();
       }
       
       editor.putString(key, value);
       editor.commit();
    }
    
    public void putValue(String key, int value)
    {
       if (null == editor)
       {
          editor = sharePref.edit();
       }
       
       editor.putInt(key, value);
       editor.commit();
    }
    
    public void putValue(String key, boolean value)
    {
       if (null == editor)
       {
          editor = sharePref.edit();
       }
       
       editor.putBoolean(key, value);
       editor.commit();
    }
    
    public String getString(String key, String defValue)
    {
       return sharePref.getString(key, defValue);
    }
    
    public int getInt(String key, int defValue)
    {
       return sharePref.getInt(key, defValue);
    }
    
    public boolean getBoolean(String key, boolean defValue)
    {
       return sharePref.getBoolean(key, defValue);
    }
}
