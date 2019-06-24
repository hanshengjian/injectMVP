package com.ca.api;

import android.app.Application;
import android.util.Log;

import com.ca.annotation.AutoWired;
import com.ca.annotation.Const;
import com.ca.annotation.model.Meta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Lenovo
 * DATE 2019/6/16
 *  This is an injection management class. Reflection performs initialization,
 * extracts the full path of the injection class from the injection table, and constructs an instance.
 *
 */
public class InjectManager {

    private final static String TAG = "InjectManager";
    private static volatile InjectManager mInstance;
    private boolean initSucc = true;
    private InjectManager() {
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

    public void inject(Object obj) {
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

    public static InjectManager getInstance() {
        if (mInstance == null) {
            synchronized (InjectManager.class) {
                if (mInstance == null) {
                    mInstance = new InjectManager();
                }
            }
        }
        return mInstance;
    }
}
