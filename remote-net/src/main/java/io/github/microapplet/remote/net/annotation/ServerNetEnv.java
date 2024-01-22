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
package io.github.microapplet.remote.net.annotation;

import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteMethodParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 三方服务网络环境,适用于在参数表中，用户通过传入方法参数，来设置网络环境
 *
 * @author Copyright  ©  <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/5/16, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@SuppressWarnings("unused")
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle({ServerNetEnv.ServerNetEnvLifeCycleHandler.class})
public @interface ServerNetEnv {

    final class ServerNetEnvLifeCycleHandler extends ApiServerEnvironmentLifeCycle implements RemoteLifeCycle.LifeCycleHandler<ServerNetEnv>{
        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, ServerNetEnv annotation) {
            methodConfig.config(NET_ENV_CONFIG,methodParameter);
        }
    }
}