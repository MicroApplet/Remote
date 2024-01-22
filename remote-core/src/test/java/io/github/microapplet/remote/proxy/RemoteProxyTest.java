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

import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteMethodParameter;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.lifecycle.callback.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Remote 代理测试 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2022/12/7, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class RemoteProxyTest {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @RemoteLifeCycle(RemoteTypeHandler.class)
    @interface RemoteType{ }

    public static final class RemoteTypeHandler implements RemoteLifeCycle.LifeCycleHandler<RemoteType>,
            io.github.microapplet.remote.lifecycle.callback.Before,
            Invoke,
            After,
            On,
            OnError,
            OnFail,
            OnRetry,
            OnSuccess,
            RetryWhen,
            SuccessWhen
    {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, @SuppressWarnings("ClassEscapesDefinedScope") RemoteType annotation) {
            System.out.println("do init");
        }

        @Override
        public void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("after");
        }

        @Override
        public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("before");
        }

        @Override
        public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("invoke");
        }

        @Override
        public void on(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("on");
        }

        @Override
        public boolean onError(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Throwable ex, Object[] args) {
            System.out.println("on error");
            return false;
        }

        @Override
        public void onFail(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("on fail");
        }

        @Override
        public void onRetry(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("on retry");
        }

        @Override
        public void onSuccess(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("on success");
        }

        @Override
        public boolean retryWhen(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("retry when");
            return false;
        }

        @Override
        public boolean success(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            System.out.println("success");
            return false;
        }
    }

    @RemoteType
    interface RemoteInterface{
        void test();
    }

    RemoteInterface remoteInterface;

    @Before public void before(){
        remoteInterface = RemoteProxy.create(RemoteInterface.class);
    }

    @Test public void testProxy(){
        remoteInterface.test();
    }
}
