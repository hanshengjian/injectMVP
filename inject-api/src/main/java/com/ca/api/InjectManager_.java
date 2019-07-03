package com.ca.api;

import android.app.Application;
import android.util.Log;
import com.ca.annotation.AutoWired;
import com.ca.annotation.Const;
import com.ca.annotation.model.Meta;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InjectManager_ {
  private final static String TAG = "InjectManager";
  private static volatile InjectManager_ mInstance;
  private boolean initSucc = true;

  public static InjectManager_ getInstance() {
    if (mInstance == null) {
      synchronized (InjectManager_.class) {
        if (mInstance == null) {
          mInstance = new InjectManager_();
        }
      }
    }
    return mInstance;
  }

  protected void inject(Object obj){
    if (!initSucc) {
      return;
    }
    Field[] fields = obj.getClass().getDeclaredFields();
    int length = fields.length;
    for (int i = 0; i < length; i++) {
      Field field = fields[i];
      if (field.isAnnotationPresent(AutoWired.class)) {
        Object instance;
        try {
          String key = field.getType().getName();
          Class<?> threadClazz = Class.forName(Const.PACKGE_NAME + "." + Const.CLASS_NAME);
          Method method = threadClazz.getMethod("getServiceImpl", String.class);
          Meta meta = (Meta) method.invoke(null,key);
          instance = Class.forName(meta.getPath()).newInstance();
          field.setAccessible(true);
          field.set(obj, instance);
        } catch (InstantiationException e) {
          Log.i(TAG,"inject failed: " + e.getMessage());
        } catch (IllegalAccessException e) {
          Log.i(TAG,"inject failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
          Log.i(TAG,"inject failed: " + e.getMessage());
        } catch (NoSuchMethodException e) {
          Log.i(TAG,"inject failed: " + e.getMessage());
        } catch (InvocationTargetException e) {
          Log.i(TAG,"inject failed: " + e.getMessage());
        }

      }
    }
  }


  public void init(Application application) {
    try {
      Class<?> threadClazz = Class.forName(Const.PACKGE_NAME + "." + Const.CLASS_NAME);
      Method method = threadClazz.getMethod("init");
      method.invoke(null);
    } catch (Exception e) {
      initSucc = false;
      Log.i(TAG, "init failed :" + e.getMessage());
    }
  }
}
