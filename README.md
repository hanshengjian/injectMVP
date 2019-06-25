# injectMVP

Interface-oriented programming and Inject some presents class's instance in activity 

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






