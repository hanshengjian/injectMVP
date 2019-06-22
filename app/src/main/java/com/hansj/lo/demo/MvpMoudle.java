package com.hansj.lo.demo;

import com.ca.annotation.Compont;

@Compont(version = 1)
public class MvpMoudle implements IModule {
    @Override
    public String getTitle() {
        return "Hello world";
    }
}
