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
import com.ca.annotation.InjectAt
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project;

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
            classPool.appendClassPath((String) it.absolutePath)
        }

        transformInvocation.inputs.each {
            TransformInput inputs ->
                inputs.jarInputs.each {
                    JarInput jarInput ->
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                                jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        //将输入内容复制到输出
                        FileUtils.copyFile(jarInput.file, dest)
                }
                inputs.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        String fileName = directoryInput.file.absolutePath;
                        File dir = new File(fileName)
                        dir.eachFileRecurse {
                            File file ->
                                CtClass ctClass = mClassPool.makeClass(new FileInputStream(file))
                                CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate",
                                        [mClassPool.makeClass("android.os.Bundle")])
                                if (ctMethod.getAnnotation(InjectAt.class) != null) {
                                    //开始 注入代码
                                    String str = """ InjectManager.getInstance().inject(this);"""
                                    ctMethod.insertAfter(str);
                                    ctClass.writeFile(dir)
                                    ctClass.detach()
                                }

                        }
                        def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes, Format.DIRECTORY)
                        // 将input的目录复制到output指定目录
                        FileUtils.copyDirectory(directoryInput.file, dest)

                }
        }
    }

}