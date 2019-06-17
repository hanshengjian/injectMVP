package com.hansj.lo.demo;

import com.ca.annotation.Compont;

@Compont(key = IModule.KEY)
public class MVPMoudle implements IModule {
    @Override
    public String getTitle() {
        return "Hello world";
    }
}
