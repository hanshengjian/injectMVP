# injectMVP

#### 项目背景

依赖注入或者控制反转思想已经体现在了java web开发的spring框架中，在android中有关注入的开源项目主要有Dagger2还有Arouter.这个项目比较优秀。在android中MVP是最合适使用依赖注入的。但Dagger2有一个缺点，就是比较繁琐，这个框架就是简化这个过程，达到轻量级的效果。



#### Compare

##### Dragger2的使用demo

需要一个module 类，一个Component接口配合，简化的代码如下；

```java
@Module
public class MainModule {
    private IView mIView;

    public MainModule(IView IView) {
        mIView = IView;
    }

    @Provides
    @ActivityScope
    public IView provideIView(){
        return this.mIView;
    }
}

```

```java
@ActivityScope
@Component(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity activity);
}

```



```java
public class MainActivity extends AppCompatActivity implements IView{

    @Inject
    MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerMainComponent
                .builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        mMainPresenter.loadData();
    }

    @Override
    public void loadSucc() {

    }
}
```

从代码层面来说，为了注入一个MainPersenter,要写这么多代码，有种不如不用的感觉，甚至在注入的时候出现new MainModule(this)这样的代码，按照道理这个过程应该交由容器来解决，不该暴露该用户的.

##### injectMvp的demo

无需配合类，一行代码实现注入过程，面向接口，不面向具体。

```java
public interface IPresenter  {
    public void loadData();

    public void bindView(IView iView);

    public void unBindView();
}
```

```java
@Compont(version = 1)
public class MainPresenter2 implements IPresenter {
    private IView mIView;

    @Override
    public void bindView(IView iView) {
        mIView = iView;
    }

    @Override
    public void unBindView() {
        mIView = null;
    }

    @Override
    public void loadData() {
        mIView.loadSucc();
    }

}
```

```java
public class MainActivity extends AppCompatActivity implements IView{
   @AutoWired
   IPresenter mIPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectManager.getInstance().init(getApplication());//放入application中初始化即可
        InjectManager.getInstance().inject(this);

        mIPresenter.bindView(this);
        mIPresenter.loadData();
    }

    @Override
    public void loadSucc() {

    }
}
```



##### 小结一下

injectMVP的处理依赖注入达到注入效果，比较简洁，版本可追述，也遵循了面向接口的思想，但是还是有可提高的地方。下面是具体的介绍以及使用方法。

#### Configuration

1. Adding maven url 

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            url "https://jitpack.io"
        }
        maven { url 'https://dl.bintray.com/josehan1989/maven' }
    }
}
```

2. Adding dependencies and configurations
   
   ```groovy
   dependencies {
       implementation 'com.ca.inject:api:1.0.2'
       annotationProcessor 'com.ca.inject:compile:1.0.2'
       implementation 'com.ca.inject:annotation:1.0.1'
   }
   ```

3. Add annotation
   
   ```java
   @Compont(version = 1)
   public class MvpPresenter implements IPresenter {
       ...
   }
   ```

4. Initiate the injecting
   
   ```java
   public class MyApp extends Application {
       @Override
       public void onCreate() {
           super.onCreate();
           InjectManager.getInstance().init(this);
       }
   }
   ```

#### Advanced usage

1. AutoWire a field
   
   ```java
   public class MvpActivity extends AppCompatActivity implements IView{
   
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
   ```
   
   #### 

#### More features

1. Higher version's class will cover lower version'class in injecting table,you only assign a version when you use Compont annotation,like this
   
   ```java
   @Compont(version = 2)
   public class MvpPresenter2 implements IPresenter {
      ...
   }
   ```

2. In a case,your higher class extends a class not implements a interface,you must add a field named of KEY,like this
   
   ```java
   @Compont(version = 3,key = MvpPresenter3.KEY)
   public class MvpPresenter3 extends MvpPresenter2 {
       public static final String KEY = "com.hansj.lo.demo.IPresenter";
   
       public MvpPresenter3(){
           init();
       }
   
       @Override
       public void getTitle() {
           mIView.refreshTitle("nihaoma");
       }
   
   }
   ```

#### In the last

I hope to improve together with you
