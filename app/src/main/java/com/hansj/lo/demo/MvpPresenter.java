package com.hansj.lo.demo;

import com.ca.annotation.AutoWired;
import com.ca.annotation.Compont;
import com.ca.api.InjectManager;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
@Compont(version = 1)
public class MvpPresenter implements IPresenter {
    private IView mIView;
    @AutoWired
    public IModule mIModule;

    public MvpPresenter(){
        init();
    }

    @Override
    public void init() {
        InjectManager.getInstance().inject(this);
    }


    @Override
    public void getTitle() {
        String title = mIModule.getTitle();
        mIView.refreshTitle(title);
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
