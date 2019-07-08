package com.hansj.lo.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ca.annotation.AutoWired;
import com.ca.annotation.InjectAt;
import com.ca.api.InjectManager;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
public class MvpActivity extends AppCompatActivity implements IView{

    @AutoWired
    IPresenter mPresenter;
    TextView tv;

    @Override
    @InjectAt
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        InjectManager.getInstance().inject(this);
        mPresenter.bind(this);
        tv = findViewById(R.id.tv);
        mPresenter.getTitle();
    }

    @Override
    public void refreshTitle(String title) {
        tv.setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unBind();
    }
}
