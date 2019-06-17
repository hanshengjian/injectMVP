package com.ca.annotationlib;

import com.ca.annotation.Compont;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author Lenovo
 * DATE 2019/6/16
 * @description
 */
@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Filer mFiler;
    private HashMap<String, String> results;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        results = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("--------start process--------");
        for (TypeElement element:annotations){
            if(element.getQualifiedName().toString().equals(Compont.class.getName())){
                handleRegisterRouter(roundEnv);
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Compont.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void handleRegisterRouter(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Compont.class);
        for (Element element : set) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;

                String key = typeElement.getAnnotation(Compont.class).key();

                results.put(key, typeElement.getQualifiedName().toString());
            }
        }

        try {
            String pkgName = "com.ca.annotationlib";
            String className = "InjectTable";
            String methodName = "getServiceImpl";
            FieldSpec fieldSpec = FieldSpec.builder(ClassName.get(HashMap.class), "injectMap", Modifier.PUBLIC,Modifier.STATIC)
                    .build();
            MethodSpec registerRouter = computeAddRouter(methodName);
            MethodSpec initMethod = generateInit();

            TypeSpec routerManger = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)
                    .addField(fieldSpec)
                    .addMethod(registerRouter)
                    .addMethod(initMethod)
                    .build();
            JavaFile javaFile = JavaFile.builder(pkgName, routerManger).build();
            javaFile.writeTo(mFiler);
        } catch (Exception e) {

        }


    }

    private MethodSpec computeAddRouter(String methodName) {
        return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Object.class)
                .addParameter(ParameterSpec.builder(String.class,"key").build())
                .addStatement("return injectMap.get(key)")
                .build();

    }

    private MethodSpec generateInit() {
        Set<Map.Entry<String,String>> set = results.entrySet();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("injectMap = new HashMap();\n");
        for (Map.Entry entry:set){
            stringBuilder.append("injectMap.put(\""+entry.getKey()+"\",\""+entry.getValue()+"\");\n");
        }
        return MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(stringBuilder.toString())
                .returns(void.class)
                .build();
    }

}
