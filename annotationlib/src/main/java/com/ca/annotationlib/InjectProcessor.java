package com.ca.annotationlib;

import com.ca.annotation.Compont;
import com.ca.annotation.Const;
import com.ca.annotation.model.Meta;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author Lenovo
 * DATE 2019/6/16
 * @description A processor create a class file by javapoet for injecting table
 */
@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Filer mFiler;
    private HashMap<String, Meta> results;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        results = new HashMap<>();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            try {
                handleRegisterCompont(roundEnv);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "handling annotation " +
                        "Compont.class make a exception:  " + e.getMessage());
            }
            return true;
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

    private void handleRegisterCompont(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Compont.class);
        for (Element element : set) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
                for (TypeMirror typeMirror : interfaces) {
                    if (results.containsKey(typeMirror.toString())) {
                        Meta meta = results.get(typeMirror.toString());
                        int version = meta.getVersion();
                        int newVersion = element.getAnnotation(Compont.class).version();
                        /**
                         * 保留高版本，删除低版本
                         */
                        if (newVersion > version) {
                            results.remove(typeMirror.toString());
                            Meta newMeta = new Meta(typeElement.getQualifiedName().toString(), newVersion);
                            results.put(typeMirror.toString(), newMeta);
                        }
                    } else {
                        int version = element.getAnnotation(Compont.class).version();
                        Meta meta = new Meta(typeElement.getQualifiedName().toString(), version);
                        results.put(typeMirror.toString(), meta);
                    }

                }
                if (CollectionUtils.isEmpty(interfaces)) {
                    String key = typeElement.getAnnotation(Compont.class).key();
                    int version = typeElement.getAnnotation(Compont.class).version();
                    if (!"".equals(key)) {
                        if (results.containsKey(key)) {
                            int oldversion = results.get(key).getVersion();
                            if (version > oldversion) {
                                results.remove(key);
                                results.put(key, new Meta(typeElement.getQualifiedName().toString(), version));
                            }
                        }
                    }
                }
            }
        }



        createClass();
    }

    private void createClass(){
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(HashMap.class,String.class,Meta.class);

        FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, "injectMap", Modifier.PUBLIC, Modifier.STATIC)
                .build();
        MethodSpec registerRouter = computeAddRouter();
        MethodSpec initMethod = generateInit();

        TypeSpec routerManger = TypeSpec.classBuilder(Const.CLASS_NAME).addModifiers(Modifier.PUBLIC)
                .addField(fieldSpec)
                .addMethod(registerRouter)
                .addMethod(initMethod)
                .addJavadoc("Inject Table provides a map")
                .build();
        JavaFile javaFile = JavaFile.builder(Const.PACKGE_NAME, routerManger).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MethodSpec computeAddRouter() {
        return MethodSpec.methodBuilder("getServiceImpl").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Meta.class)
                .addParameter(ParameterSpec.builder(String.class, "key").build())
                .addStatement("return injectMap.get(key)")
                .build();

    }

    private MethodSpec generateInit() {
        MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class);

        Set<Map.Entry<String, Meta>> set = results.entrySet();
        initBuilder.addStatement("injectMap = new HashMap<$T,Meta>()",ClassName.get(String.class));
        for (Map.Entry entry : set) {
            Meta meta = (Meta) entry.getValue();
            initBuilder.addStatement("injectMap.put(\"" + entry.getKey() + "\",$T.build(\"" + meta.getPath() + "\"," + meta.getVersion() + "))",
                    ClassName.get(Meta.class));
        }
        return initBuilder
                .build();
    }

}
