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

import io.github.microapplet.remote.context.*;
import io.github.microapplet.remote.lifecycle.callback.Before;
import io.github.microapplet.remote.lifecycle.callback.On;
import io.github.microapplet.remote.net.repository.ApiServerEnvironmentHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 网络环境处理器
 *
 * @author Copyright  ©  <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/5/16, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class ApiServerEnvironmentLifeCycle implements Before, On {
    public static final GenericKey<String> NET_ENV_KEY = GenericKey.keyOf("net_env_key");
    public static final GenericKey<RemoteMethodParameter> NET_ENV_CONFIG = GenericKey.keyOf("net_env_key");
    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        // 请求上下文当中已配置直接响应
        if (req.containsKey(NET_ENV_KEY))
            return;

        // 从线程中获取环境配置
        String threadEnv = ApiServerEnvironmentHolder.get();
        String localEnv = StringUtils.EMPTY;

        // 从配置注册表中获取环境配置
        RemoteMethodParameter parameter = methodConfig.config(NET_ENV_CONFIG);
        if (Objects.nonNull(parameter)){
            int index = parameter.getIndex();
            Object arg = args[index];
            if (arg instanceof String){
                localEnv = (String) arg;
            }
        }

        // 局部环境配置
        if (StringUtils.isNotBlank(localEnv)){
            req.put(NET_ENV_KEY, localEnv);
            return;
        }

        // 线程环境配置
        if (StringUtils.isNotBlank(threadEnv)){
            req.put(NET_ENV_KEY,threadEnv);
            return;
        }

        // 全局环境配置
        req.put(NET_ENV_KEY, ServerLifeCycle.GLOBAL_NET_ENV);
    }

    @Override
    public void on(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        ApiServerEnvironmentHolder.clean();
    }
}