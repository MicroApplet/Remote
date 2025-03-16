/*
 *  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.asialjim.microapplet.remote.context;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.lifecycle.CallBack;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RemoteMethodConfig implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(RemoteMethodConfig.class);
    @Getter
    private transient final RemoteHandlerContext remoteHandlerContext;
    @Getter
    private transient final String remoteName;
    private transient final Class<?> remoteInterface;
    private transient final Method method;
    private transient final List<Annotation> typeAnnotations;
    private transient final List<Annotation> methodAnnotations;
    private transient final List<RemoteMethodParameter> remoteMethodParameters;
    private transient final List<Annotation> returnAnnotations;
    @Getter
    private transient final Type returnType;
    @Getter
    private transient final Class<?> returnClass;
    private transient final RemoteGenericMap configMap;

    private RemoteMethodConfig(Class<?> remoteInterface, Method method) {
        this.remoteHandlerContext = new RemoteHandlerContext();
        this.remoteInterface = remoteInterface;
        this.method = method;
        this.typeAnnotations = new ArrayList<>();
        this.methodAnnotations = new ArrayList<>();
        this.remoteMethodParameters = new ArrayList<>();
        this.returnAnnotations = new ArrayList<>();
        this.returnType = method.getGenericReturnType();
        this.returnClass = method.getReturnType();
        this.configMap = new RemoteGenericMap();
        this.remoteName = this.remoteInterface.getSimpleName() + "#" + this.method.getName();
        // 类上注解
        if (log.isDebugEnabled()) log.debug("Process Annotation on Type: {}", this.remoteInterface.getSimpleName());

        this.typeAnnotations.addAll(Arrays.asList(remoteInterface.getAnnotations()));

        // 方法上注解
        if (log.isDebugEnabled()) log.debug("Process Annotation on Method: {}", this.remoteName);
        this.methodAnnotations.addAll(Arrays.asList(method.getAnnotations()));

        // 返回注解
        if (log.isDebugEnabled())
            log.debug("Process Annotation on ReturnType: {}", this.returnClass.getSimpleName() + "@" + this.remoteName);
        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();
        this.returnAnnotations.addAll(Arrays.asList(annotatedReturnType.getAnnotations()));

        // 参数注解
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            RemoteMethodParameter methodParameter = RemoteMethodParameter.create(i, parameters[i]);
            if (Objects.isNull(methodParameter))
                continue;

            if (log.isDebugEnabled())
                log.debug("Process Annotation on Method: [{}, Parameter Index: {}]", this.remoteName, methodParameter.getIndex());
            this.remoteMethodParameters.add(methodParameter);
        }

        RemoteLifeCycleHandlerFactory.FACTORY.addLifeCycles(this.remoteHandlerContext);
    }

    public static RemoteMethodConfig create(Class<?> remoteInterface, Method method) {
        return new RemoteMethodConfig(remoteInterface, method);
    }

    public <Config> Config config(GenericKey<Config> key) {
        return this.configMap.valueOf(key);
    }

    public <Config> void config(GenericKey<Config> key, Config configValue) {
        this.configMap.valueOf(key,configValue);
    }

    public RemoteMethodConfig init() {
        // 初始化方法参数
        if (log.isDebugEnabled())
            log.debug("Init Parameters on Method: {}", this.remoteInterface.getSimpleName() + "#" + this.method.getName());
        initParameters();

        // 初始化方法
        if (log.isDebugEnabled())
            log.debug("Init Method: {}", this.remoteInterface.getSimpleName() + "#" + this.method.getName());
        initMethod();

        // 初始化响应
        if (log.isDebugEnabled())
            log.debug("Init Return Type on Method: {}", this.remoteInterface.getSimpleName() + "#" + this.method.getName());
        initReturn();

        // 初始化类
        if (log.isDebugEnabled())
            log.debug("Init Type of Method: {}", this.remoteInterface.getSimpleName() + "#" + this.method.getName());
        initType();
        return this;
    }

    private void initMethod() {
        this.methodAnnotations.forEach(annotation -> initAnnotation(annotation, null));
    }

    private void initReturn() {
        this.returnAnnotations.forEach(annotation -> initAnnotation(annotation, null));
    }

    private void initType() {
        this.typeAnnotations.forEach(annotation -> initAnnotation(annotation, null));
    }

    private void initParameters() {
        for (RemoteMethodParameter methodParameter : this.remoteMethodParameters) {
            Parameter parameter = methodParameter.getParameter();
            // 参数是回调函数
            if (CallBack.class.isAssignableFrom(parameter.getType())) {
                this.remoteHandlerContext.addCallBackIndex(methodParameter.getIndex());
                continue;
            }

            List<Annotation> annotations = methodParameter.getParameterAnnotations();
            annotations.forEach(annotation -> initAnnotation(annotation, methodParameter));
        }
    }

    private void initAnnotation(Annotation annotation, RemoteMethodParameter parameter) {
        if (Objects.isNull(annotation))
            return;

        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (Objects.isNull(annotationType))
            return;

        RemoteLifeCycle remoteLifeCycle = annotationType.getAnnotation(RemoteLifeCycle.class);
        if (Objects.isNull(remoteLifeCycle))
            return;

        Class<? extends RemoteLifeCycle.LifeCycleHandler<?>>[] remoteLifeCycleHandlerClasses = remoteLifeCycle.value();
        for (Class<? extends RemoteLifeCycle.LifeCycleHandler<?>> remoteLifeCycleHandlerClass : remoteLifeCycleHandlerClasses) {
            if (Objects.isNull(remoteLifeCycleHandlerClass))
                continue;

            //noinspection rawtypes
            RemoteLifeCycle.LifeCycleHandler handler = RemoteLifeCycleHandlerFactory.FACTORY.singletonHandler(remoteLifeCycleHandlerClass);
            if (Objects.isNull(handler))
                continue;

            if (log.isDebugEnabled()) log.debug("LifeCycle：{} Init Start...", handler);

            //noinspection unchecked
            handler.init(this, parameter, annotation);
            this.remoteHandlerContext.addLifeCycle(handler);
        }
    }

    @Override
    public RemoteMethodConfig clone() {
        try {
            return (RemoteMethodConfig) super.clone();
        } catch (Throwable e) {
            return this;
        }
    }
}