# injectMVP

Interface-oriented programming and Inject some presents class's instance in activity 



### A demo shows usage

```java
package com.hansj.lo.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ca.annotation.AutoWired;
import com.ca.annotationapi.InjectManager;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
public class MVPActivity extends AppCompatActivity implements IView{

    @AutoWired
    IPresenter mPresenter;
    TextView tv;

    @Override
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
//=========================
package com.hansj.lo.demo;

import com.ca.annotation.AutoWired;
import com.ca.annotation.Compont;
import com.ca.annotationapi.InjectManager;

/**
 * @author Lenovo
 * DATE 2019/6/16
 */
@Compont(key = IPresenter.KEY)
public class MVPPresenter implements IPresenter {
    private IView mIView;
    @AutoWired
    public IModule mIModule;

    public MVPPresenter(){
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

//==================================

package com.hansj.lo.demo;

import com.ca.annotation.Compont;

@Compont(key = IModule.KEY)
public class MVPMoudle implements IModule {
    @Override
    public String getTitle() {
        return "Hello world";
    }
}

```




