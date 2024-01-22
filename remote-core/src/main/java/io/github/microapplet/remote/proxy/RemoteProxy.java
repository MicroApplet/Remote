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
package io.github.microapplet.remote.proxy;

import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.loader.RemoteClassLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Remote 代理服务
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/10 &nbsp;&nbsp; 1.0 &nbsp;&nbsp; JDK 8
 */
public class RemoteProxy implements InvocationHandler {
    private transient final String desc;
    private transient final Map<Method, RemoteMethodInvoker> methodCache;

    static {
        RemoteClassLoader.INSTANCE.init();
    }

    public static<T> T create(Class<T> remoteInterface){
        if (Objects.isNull(remoteInterface))
            throw new IllegalArgumentException("Remote Proxy Interface Class Cannot be Null");
        if (!remoteInterface.isInterface())
            throw new IllegalArgumentException("Remote Proxy Interface Class must be Interface");

        RemoteProxy remoteProxy = new RemoteProxy(remoteInterface);
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(remoteInterface.getClassLoader(), new Class[]{remoteInterface}, remoteProxy);
    }

    private RemoteProxy(Class<?> remoteInterface){
        this.methodCache = new HashMap<>();
        this.desc = "RemoteProxyOf[" + remoteInterface.getName() + "]";
        initClass(remoteInterface);
    }

    private void initClass(Class<?> remoteInterface){initMethod(remoteInterface);}

    private void initMethod(Class<?> clazz){
        for (Class<?> superClass : clazz.getInterfaces())
            initMethod(superClass);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isDefault() || Modifier.isStatic(method.getModifiers()))
                continue;

            RemoteMethodConfig config = RemoteMethodConfig.create(clazz, method).init();
            RemoteMethodInvoker invoker = new RemoteMethodInvoker(config);
            methodCache.put(method,invoker);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass()) || method.isDefault() || Modifier.isStatic(method.getModifiers()))
            return method.invoke(this, args);

        return this.methodCache.get(method).invoke(args);
    }

    @Override public String toString() {return this.desc;}
}
