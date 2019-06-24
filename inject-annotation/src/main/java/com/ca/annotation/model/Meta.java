package com.ca.annotation.model;

/**
 * @author Lenovo
 * DATE 2019/6/22
 */
public class Meta {
    private String path;//实现类的path;
    private int version;//版本

    public Meta(String path, int version) {
        this.path = path;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public int getVersion() {
        return version;
    }

    public static Meta build(String path,int versison){
        Meta meta = new Meta(path,versison);
        return meta;
    }

}
