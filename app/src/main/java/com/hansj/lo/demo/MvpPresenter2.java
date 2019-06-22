package com.hansj.lo.demo;

import com.ca.annotation.AutoWired;
import com.ca.annotation.Compont;
import com.ca.annotationapi.InjectManager;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
@Compont(version = 2)
public class MvpPresenter2 implements IPresenter {
    private IView mIView;
    @AutoWired
    public IModule mIModule;

    public MvpPresenter2(){
        init();
    }

    @Override
    public void init() {
        InjectManager.getInstance().inject(this);
    }


    @Override
    public void getTitle() {
        mIView.refreshTitle("老婆大人");
    }

    @Override
    public void bind(IView view) {
        mIView = view;
    }

    @Override
    public void unBind() {
        mIView = null;
    }


}
