//package com.ca.api;
//
//import android.util.Log;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//
//@Aspect
//public class AspectJInjectManager {
//
//  public static final String TAG = "AspectJInjectManager";
//  private static final String POINTCUT_METHOD = "execution(* *..MvpActivity+.on**(..))";
//
//  @Pointcut(POINTCUT_METHOD)
//  public void executionFreeInject(){
//
//  }
//
//  @Before("executionFreeInject()")
//  public void freeeInject(final JoinPoint joinPoint) throws Throwable{
//    Log.i(TAG,"start freeeInject");
////    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
////    FreeInject freeInject = methodSignature.getMethod().getAnnotation(FreeInject.class);
////    if(freeInject !=null) {
////      Object object = joinPoint.getThis();
////      InjectManager.getInstance().inject(object);
////      joinPoint.proceed();
////    }
//  }
//
//}
