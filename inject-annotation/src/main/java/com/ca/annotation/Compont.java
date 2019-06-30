package com.ca.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lenovo
 * DATE 2019/6/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Compont {
   /**
    * 优先提供高版本的实例
    * @return
    */
   public int version();

   public Class<?> key() default IBase.class;


}
