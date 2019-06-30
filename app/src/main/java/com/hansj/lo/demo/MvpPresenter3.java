package com.hansj.lo.demo;

import com.ca.annotation.Compont;


/**
 * @author Lenovo
 * DATE 2019/6/16
 * <p>因为是继承，无法获知这个类实现的接口，但可以通过字符串KEY定义实现的接口名
 *
 * </>
 */
@Compont(version = 3,key = IPresenter.class)
public class MvpPresenter3 extends MvpPresenter2 {

    public MvpPresenter3(){
        init();
    }

    @Override
    public void getTitle() {
        mIView.refreshTitle("nihaoma");
    }

}
