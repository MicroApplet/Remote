/*
 * Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
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
package com.asialjim.microapplet.remote.net.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle({ServerLifeCycle.class, Server.ServerNetLifeCycle.class})
public @interface Server {
    /**
     * String Bean Name
     */
    String name() default StringUtils.EMPTY;

    /**
     * http/https/tcp/udp/ftp...
     */
    String schema() default StringUtils.EMPTY;

    /**
     * 主机名或者IP地址
     */
    String host() default StringUtils.EMPTY;

    /**
     * 端口
     */
    int port() default 0;

    /**
     * 代理主机名或者IP地址
     */
    String proxyHost() default StringUtils.EMPTY;

    /**
     * 代理端口
     */
    int proxyPort() default 0;

    /**
     * 超时时间
     */
    int timeout() default 5000;

    /**
     * 字符集
     */
    String charset() default "UTF-8";

    /**
     * API 供应商
     */
    String supplier() default StringUtils.EMPTY;

    /**
     * API 业务环境
     */
    String namespace() default StringUtils.EMPTY;

    /**
     * 系统环境
     */
    String env() default GLOBAL_NET_ENV;
    String GLOBAL_NET_ENV = ServerLifeCycle.GLOBAL_NET_ENV;

    final class ServerNetLifeCycle extends ApiServerEnvironmentLifeCycle implements RemoteLifeCycle.LifeCycleHandler<Server>{

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, Server annotation) {
            // do nothing here
        }
    }
}