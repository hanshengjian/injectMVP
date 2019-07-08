package com.ca.injectPlugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project;

class InjectPlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new InjectTransForm(project))
    }
}