package com.ca.injectPlugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.SdkConstants
import com.ca.annotation.InjectAt
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project;
import org.apache.commons.codec.digest.DigestUtils

import java.lang.annotation.Annotation

class InjectTransForm extends Transform {
    private Project mProject;
    private ClassPool mClassPool;

    public InjectTransForm(Project project) {
        this.mProject = project;
    }

    @Override
    String getName() {
        return "InjectTransForm"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        mClassPool = new ClassPool()
        mProject.android.bootClasspath.each {
            println("bootClasspath:" + it.absolutePath)
            mClassPool.appendClassPath((String) it.absolutePath)
        }

        transformInvocation.inputs.each {
            TransformInput inputs ->
                inputs.jarInputs.each {
                    JarInput jarInput ->
// 重命名输出文件（同目录copyFile会冲突）
                        File dest = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        // 复制jar到目标目录
                        FileUtils.copyFile(jarInput.file, dest)

                        mClassPool.appendClassPath(dest.toString())

                       // println("jar = " + dest)

                }
                inputs.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        String fileName = directoryInput.file.absolutePath;
                        File dir = new File(fileName)

                        mClassPool.appendClassPath(fileName)
                        mClassPool.appendClassPath(mProject.android.bootClasspath[0].toString())
                     //   mClassPool.importPackage("android.os.Bundle")
                        dir.eachFileRecurse {
                            File file ->
                                String filePath = file.absolutePath
                                def className = filePath.replace(fileName, "")
                                        .replace("\\", ".")
                                        .replace("/", ".")
                                def name = className.replace(SdkConstants.DOT_CLASS, "")
                                        .substring(1)

                               // println("className:" + className + ": name;" + name )
                                if (className.endsWith(".class") && !className.contains('R$') && !className.contains('$')//代理类
                                        && !className.contains('R.class') && !className.contains("BuildConfig.class")){
                                    CtClass ctClass = mClassPool.getCtClass(name)
                                    CtMethod[] ctMethods = ctClass.getDeclaredMethods()
                                    for (CtMethod ctMethod:ctMethods){
                                        String method = ctMethod.getName();
                                        println("xxmethod:" + method)
                                        if(method.contains(Const.METHOD_NAME_INJECT)){
                                            for (Annotation annotation:ctMethod.getAnnotations()){
                                                if(annotation.annotationType().canonicalName.equals(Const.ANNOTATION_NAME_INJECT)){
                                                    ctMethod.insertBefore(Const.CODE_INJECT);
                                                    ctClass.writeFile(fileName)
                                                    ctClass.detach()
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                        def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes, Format.DIRECTORY)
                        println("xxdest" + dest)
                        // 将input的目录复制到output指定目录
                        FileUtils.copyDirectory(directoryInput.file, dest)

                }
        }
    }

}