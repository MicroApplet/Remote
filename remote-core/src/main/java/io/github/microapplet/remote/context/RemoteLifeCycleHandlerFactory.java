/*
 * Copyright 2014-2023 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.microapplet.remote.context;

import io.github.microapplet.remote.annotation.Primary;
import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.annotation.RemoteSubProperty;
import io.github.microapplet.remote.lifecycle.LifeCycle;
import io.github.microapplet.remote.loader.RemoteClassLoader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Remote 生命周期处理器工厂
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/10 &nbsp;&nbsp; 1.0 &nbsp;&nbsp; JDK 8
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoteLifeCycleHandlerFactory {
    public static final RemoteLifeCycleHandlerFactory FACTORY = new RemoteLifeCycleHandlerFactory();

    private static final Map<Class<? extends RemoteLifeCycle.LifeCycleHandler<?>>, RemoteLifeCycle.LifeCycleHandler<?>> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private static final List<LifeCycle> LIFE_CYCLES = new Vector<>();
    private static String remotePrimaries = StringUtils.EMPTY;
    private static List<String> remotePrimarieList = null;

    public static void primary(String remotePrimaries) {
        if (StringUtils.isNotBlank(RemoteLifeCycleHandlerFactory.remotePrimaries) || StringUtils.isBlank(remotePrimaries))
            return;
        RemoteLifeCycleHandlerFactory.remotePrimaries = remotePrimaries;
    }

    public static boolean primarySet() {
        return StringUtils.isNotBlank(RemoteLifeCycleHandlerFactory.remotePrimaries);
    }

    private static List<String> primaries() {
        if (Objects.nonNull(remotePrimarieList))
            return remotePrimarieList;

        synchronized (RemoteLifeCycleHandlerFactory.class) {
            if (CollectionUtils.isNotEmpty(remotePrimarieList))
                return remotePrimarieList;

            if (StringUtils.isBlank(remotePrimaries))
                remotePrimaries = System.getProperty("remote.primary");
            if (StringUtils.isNotBlank(remotePrimaries))
                remotePrimarieList = Arrays.asList(remotePrimaries.split("[,;]"));
            if (Objects.isNull(remotePrimaries))
                remotePrimarieList = Collections.emptyList();
            return remotePrimarieList;
        }
    }

    @SuppressWarnings("unused")
    public void addHandler(Class<? extends RemoteLifeCycle.LifeCycleHandler<?>> clazz, RemoteLifeCycle.LifeCycleHandler<?> bean) {
        INSTANCE_CACHE.putIfAbsent(clazz, bean);
    }

    @SuppressWarnings("unused")
    public void addHandler(LifeCycle lifeCycle) {
        if (Objects.nonNull(lifeCycle))
            LIFE_CYCLES.add(lifeCycle);
    }

    public void addLifeCycles(RemoteHandlerContext context) {
        if (Objects.isNull(context))
            return;
        LIFE_CYCLES.forEach(context::addLifeCycle);
    }

    public RemoteLifeCycle.LifeCycleHandler<?> singletonHandler(Class<? extends RemoteLifeCycle.LifeCycleHandler<?>> clazz) {
        RemoteLifeCycle.LifeCycleHandler<?> handler = INSTANCE_CACHE.get(clazz);
        if (Objects.nonNull(handler))
            return handler;

        String name = clazz.getName();
        synchronized (INSTANCE_CACHE) {
            handler = INSTANCE_CACHE.get(clazz);
            if (Objects.nonNull(handler))
                return handler;

            // 接口
            if (Modifier.isInterface(clazz.getModifiers()))
                throw new IllegalStateException("不能为接口：" + name + "构建实例");

            // 非抽象类
            if (!Modifier.isAbstract(clazz.getModifiers()))
                return constructInstance(clazz, name);

            // 抽象类
            // 获取抽象类子类
            Set<Class<?>> subClasses = RemoteClassLoader.subClasses(clazz);
            if (CollectionUtils.isEmpty(subClasses))
                throw new IllegalStateException(clazz.getName() + " 为抽象类，但未提供子类");

            // 仅有一个选择
            if (CollectionUtils.size(subClasses) == 1) {
                //noinspection unchecked,OptionalGetWithoutIsPresent
                clazz = (Class<? extends RemoteLifeCycle.LifeCycleHandler<?>>) subClasses.stream().findFirst().get();
                return constructInstance(clazz, name);
            }

            // 暂未选到可用子类
            // 设置优先级
            if (primarySet()) {
                //noinspection unchecked
                Class<? extends RemoteLifeCycle.LifeCycleHandler<?>> candidate = (Class<? extends RemoteLifeCycle.LifeCycleHandler<?>>) subClasses.stream()
                        // 选取被 RemoteSubProperty 标注的类
                        .filter(item -> item.isAnnotationPresent(RemoteSubProperty.class))
                        // 判断 RemoteSubProperty # value 属性是否 符合配置的优先策略
                        .filter(item -> primary(item.getAnnotation(RemoteSubProperty.class).value()))
                        .findFirst().orElse(null);

                if (Objects.nonNull(candidate))
                    return constructInstance(candidate, name);
            }

            // 通过优先级筛选后，依然没有获取到可用子类
            List<Class<?>> candidates = subClasses.stream().filter(item -> item.isAnnotationPresent(Primary.class)).collect(Collectors.toList());
            if (CollectionUtils.size(candidates) == 1) {
                // noinspection unchecked
                clazz = (Class<? extends RemoteLifeCycle.LifeCycleHandler<?>>) candidates.getFirst();
                return constructInstance(clazz, name);
            }

            throw new IllegalStateException(clazz.getName() + " 存在多个子类，但没有子类符合优先匹配原则： " + primaries());
        }
    }

    private RemoteLifeCycle.LifeCycleHandler<?> constructInstance(Class<? extends RemoteLifeCycle.LifeCycleHandler<?>> clazz, String name) {
        String clazzName = clazz.getName();
        Constructor<?> constructor = Arrays.stream(clazz.getConstructors()).filter(item -> item.getParameterCount() == 0).findFirst().orElseThrow(() -> new RuntimeException("RemoteLifeCycleHandler [" + clazzName + "] can not found default constructor..."));
        try {
            if (Modifier.PUBLIC != constructor.getModifiers())
                throw new IllegalArgumentException("Default Constructor of Class<" + clazz.getName() + "> must be public");
            RemoteLifeCycle.LifeCycleHandler<?>
                    handler = (RemoteLifeCycle.LifeCycleHandler<?>) constructor.newInstance();
            INSTANCE_CACHE.put(clazz, handler);
            return handler;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("无法为Class: " + clazzName + ", [目标类：" + name + "] 构建实例, 异常：" + e.getMessage());
        }
    }

    private boolean primary(String[] subProperty) {
        if (Objects.isNull(subProperty) || ArrayUtils.getLength(subProperty) == 0)
            return false;

        List<String> primaries = primaries();
        for (String property : subProperty) {
            if (primaries.stream().anyMatch(item -> StringUtils.containsIgnoreCase(property, item))) {
                return true;
            }
        }

        return false;
    }
}