package com.ca.injectPlugin

public class Const{
    final static String ANNOTATION_NAME_INJECT = "com.ca.annotation.InjectAt";
    final static String METHOD_NAME_INJECT = "onCreate";
    final static String CODE_INJECT =  """ com.ca.api.InjectManager.getInstance().inject(this);""";
}