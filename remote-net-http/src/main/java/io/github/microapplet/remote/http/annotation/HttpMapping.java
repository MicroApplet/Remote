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
package io.github.microapplet.remote.http.annotation;


import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteMethodParameter;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpProcessLifeCycle;
import io.github.microapplet.remote.net.annotation.ServerLifeCycle;
import io.github.microapplet.remote.net.client.RemoteNetClient;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP 请求
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/13, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle({HttpMapping.HttpMappingLifeCycle.class, HttpMapping.HttpProcessLifeCycle.class})
public @interface HttpMapping {

    String method();

    String uri();

    HttpQuery[] queries() default {};

    HttpHeader[] headers() default {};

    final class HttpMappingLifeCycle extends AbstractHttpMappingLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpMapping> {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, HttpMapping annotation) {
            // 配置 请求方法
            methodConfig.config(HTTP_METHOD_KEY, annotation.method());

            // 配置 请求链接
            methodConfig.config(HTTP_REQUEST_URI, annotation.uri());

            HttpHeader[] headers = annotation.headers();
            HttpQuery[] queries = annotation.queries();

            // 设置 通用 请求头
            if (ArrayUtils.isNotEmpty(headers)) {
                Map<String, String> headerMap = new HashMap<>();
                for (HttpHeader header : headers) {
                    headerMap.put(header.name(), header.value());
                }
                methodConfig.config(COMMON_HEADER, headerMap);
            }

            // 设置 通用 查询参数
            if (ArrayUtils.isNotEmpty(queries)) {
                Map<String, String> queryMap = new HashMap<>();
                for (HttpQuery query : queries) {
                    queryMap.put(query.name(), query.value());
                }
                methodConfig.config(COMMON_QUERY, queryMap);
            }
        }
    }

    abstract class HttpProcessLifeCycle extends AbstractHttpProcessLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpMapping>{
        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, HttpMapping annotation) {
        }

        @Override
        public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            doBefore(data, methodConfig, req, res, args);
            RemoteNetNodeKey nodeKey = req.get(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY);
            RemoteNetClient client = RemoteNetClient.REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP.get(nodeKey);
            if (Objects.isNull(client)){
                client = newRemoteNetClient(nodeKey);
                RemoteNetClient.REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP.put(nodeKey,client);
            }
            req.put(RemoteNetClient.REMOTE_NET_CLIENT_GENERIC_KEY, client);
            afterBefore(data, methodConfig, req, res, args);
        }

        @SuppressWarnings("unused")
        protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args){
            // do nothing here
        }
        @SuppressWarnings("unused")
        protected void afterBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args){
            // do nothing here
        }
        protected abstract RemoteNetClient newRemoteNetClient(RemoteNetNodeKey nodeKey);
    }
}