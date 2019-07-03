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

    private static volatile InjectManager mInstance;
    private InjectManager() {
        mInjectManager_ = InjectManager_.getInstance();
    }

    private InjectManager_ mInjectManager_;

    public void init(Application application) {
        mInjectManager_.init(application);
    }

    public void inject(Object obj) {
        mInjectManager_.inject(obj);
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
