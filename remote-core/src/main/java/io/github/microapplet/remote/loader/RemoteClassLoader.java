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
package io.github.microapplet.remote.loader;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remote 类加载器
 *
 * @author Copyright  © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/9/25, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class RemoteClassLoader extends ClassLoader {
    public static final RemoteClassLoader INSTANCE = new RemoteClassLoader();
    private static final Logger log = LoggerFactory.getLogger(RemoteClassLoader.class);
    private static final String REMOTE_CLASSES_CONFIG = "META-INF/remote.loadRemoteClass";
    private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();
    private static final SetValuedMap<Class<?>, Class<?>> REMOTE_SUB_CLASSES = MultiMapUtils.newSetValuedHashMap();
    private static ClassLoader classLoader;
    private volatile boolean init = false;

    public static void classLoader(ClassLoader classLoader) {
        if (Objects.isNull(classLoader))
            return;
        RemoteClassLoader.classLoader = classLoader;
    }

    private static ClassLoader classLoader() {
        return Optional.ofNullable(RemoteClassLoader.classLoader).orElse(RemoteClassLoader.class.getClassLoader());
    }

    public static Set<Class<?>> subClasses(Class<?> remoteClass) {
        return Optional.ofNullable(REMOTE_SUB_CLASSES.get(remoteClass)).orElseGet(HashSet::new);
    }

    public static void subClassOf(Class<?> remoteClass, Class<?> subRemoteClass) {
        if (Objects.isNull(remoteClass) || Objects.isNull(subRemoteClass))
            return;
        if (!remoteClass.isAssignableFrom(subRemoteClass))
            throw new IllegalArgumentException(subRemoteClass.getName() + " 不是" + remoteClass.getName() + " 的子类");

        REMOTE_SUB_CLASSES.put(remoteClass, subRemoteClass);
    }

    public void init() {
        if (init)
            return;
        ClassLoader classLoader = classLoader();
        log.info("RemoteClassLoader init, Target ClassLoader: {}", classLoader);
        try {
            Enumeration<URL> resources = classLoader.getResources(REMOTE_CLASSES_CONFIG);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                InputStream inputStream = null;
                try {
                    inputStream = url.openStream();
                    if (Objects.isNull(inputStream))
                        continue;
                    List<String> list = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
                    for (String line : list) {
                        if (StringUtils.isBlank(line) || StringUtils.startsWith(StringUtils.trim(line),"#"))
                            continue;
                        Class<?> aClass;
                        try {
                            log.info("Load Class: {}", line);
                            aClass = loadClass(line);
                            superClasses(aClass, aClass.getSuperclass());
                            superInterfaces(aClass, aClass.getInterfaces());
                        } catch (ClassNotFoundException e) {
                            log.info("Load Class: {}, exception: {}", line,e.getMessage(),e);
                        }
                    }
                } catch (IOException e) {
                    log.info("Load Url: {}, exception: {}", url, e.getMessage(),e);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        } catch (Throwable e) {
            log.info("Load Resources: {}, exception: {}", REMOTE_CLASSES_CONFIG, e.getMessage(),e);
            throw new RuntimeException(e);
        }
        init = true;
    }

    private void superClasses(Class<?> source, Class<?> target) {
        if (Objects.isNull(target) || StringUtils.startsWith(target.getName(), "java"))
            return;

        subClassOf(target, source);
        superInterfaces(source, target.getInterfaces());
        Class<?> superclass = target.getSuperclass();
        superClasses(source, superclass);
    }

    private void superInterfaces(Class<?> source, Class<?>[] target) {
        if (ArrayUtils.isEmpty(target))
            return;

        for (Class<?> superInterface : target) {
            if (Objects.isNull(superInterface) || StringUtils.startsWith(superInterface.getName(), "java"))
                continue;

            subClassOf(superInterface, source);
            Class<?>[] interfaces = superInterface.getInterfaces();
            superInterfaces(source, interfaces);
        }
    }

    @SuppressWarnings("unused")
    public Map<String, Class<?>> classMap() {
        return CLASS_MAP;
    }

    @SuppressWarnings("unused")
    public SetValuedMap<Class<?>, Class<?>> remoteSubClasses() {
        return REMOTE_SUB_CLASSES;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> aClass = CLASS_MAP.get(name);
        if (Objects.nonNull(aClass))
            return aClass;

        try {
            aClass = classLoader().loadClass(name);
            if (Objects.nonNull(aClass)) {
                CLASS_MAP.putIfAbsent(name, aClass);
                return aClass;
            }
        } catch (Throwable t) {
            aClass = super.loadClass(name);
            CLASS_MAP.putIfAbsent(name, aClass);
        }

        return aClass;
    }
}