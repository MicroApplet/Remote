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
package io.github.microapplet.remote.net.constant;

import io.github.microapplet.remote.context.GenericKey;

/**
 * 常量池
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2022/12/9, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public interface RemoteConstant {

    /**协议：tcp/udp/http/https...,类型：String*/
    String SCHEMA_VALUE = "_SCHEMA";
    /**host 主机名,类型：String*/
    String HOST_VALUE = "_HOST";
    /**端口,类型：int*/
    String PORT_VALUE = "_PORT";

    /**代理主机名,类型： String*/
    String PROXY_HOST_VALUE = "_PROXY_HOST";
    /**代理端口, 类型： int*/
    String PROXY_PORT_VALUE = "_PROXY_PORT";

    /**API 供应商, 类型： String*/
    String SUPPLIER_VALUE = "_SUPPLIER";
    /**API 业务空间, 类型： String*/
    String NAMESPACE_VALUE = "_NAMESPACE";

    /**API 环境, 类型： String*/
    String ENV_VALUE = "_ENV";
    /**超时时间, 类型： int*/
    String TIMEOUT_VALUE = "_TIMEOUT";
    /**字符集, 类型： String*/
    String CHARSET_VALUE = "_CHARSET";

    GenericKey<String> SCHEMA = GenericKey.keyOf(SCHEMA_VALUE);
    GenericKey<String> HOST = GenericKey.keyOf(HOST_VALUE);
    GenericKey<Integer> PORT = GenericKey.keyOf(PORT_VALUE);
    GenericKey<String> PROXY_HOST = GenericKey.keyOf(PROXY_HOST_VALUE);
    GenericKey<Integer> PROXY_PORT = GenericKey.keyOf(PROXY_PORT_VALUE);
    GenericKey<String> SUPPLIER = GenericKey.keyOf(SUPPLIER_VALUE);
    GenericKey<String> NAMESPACE = GenericKey.keyOf(NAMESPACE_VALUE);
    GenericKey<String> ENV = GenericKey.keyOf(ENV_VALUE);
    GenericKey<Integer> TIMEOUT = GenericKey.keyOf(TIMEOUT_VALUE);
    GenericKey<String> CHARSET  = GenericKey.keyOf(CHARSET_VALUE);
}