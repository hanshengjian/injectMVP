package com.hansj.lo.demo;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
public interface IPresenter {

     String KEY = "com.hansj.lo.myapplication2.IPresenter";

     void getTitle();

     void bind(IView view);

     void unBind();

     void init();

}
