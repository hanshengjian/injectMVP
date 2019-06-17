package com.hansj.lo.demo;

import android.app.Application;
import com.ca.annotationapi.InjectManager;
/**
 * @author Lenovo
 * DATE 2019/6/16
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        InjectManager.getInstance().init(this);
    }
}
