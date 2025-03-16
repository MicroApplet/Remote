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
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpProcessLifeCycle;
import com.asialjim.microapplet.remote.net.annotation.ServerLifeCycle;
import com.asialjim.microapplet.remote.net.client.RemoteNetClient;
import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;

import java.lang.annotation.*;
import java.util.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle({HttpMapping.HttpMappingLifeCycle.class, HttpMapping.HttpProcessLifeCycle.class})
public @interface HttpMapping {

    /**
     * HTTP 方法
     * @since 2024/3/8
     */
    String method();

    /**
     * HTTP url 链接，  url-path 格式如下：  <a href="https://www.baidu.com/ab/cd/#{aaa}/${dddd}">...</a>
     */
    String uri();

    /**
     * queries 常量查询参数
     */
    HttpQuery[] queries() default {};

    /**
     * headers 常量请求头
     */
    HttpHeader[] headers() default {};

    final class HttpMappingLifeCycle extends AbstractHttpMappingLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpMapping> {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, HttpMapping annotation) {
            // 配置 请求方法
            methodConfig.config(HTTP_METHOD_KEY, annotation.method());

            // 配置 请求链接
            methodConfig.config(HTTP_REQUEST_URI, annotation.uri());

            HttpHeader[] headers = Optional.ofNullable(annotation.headers()).orElseGet(() -> new HttpHeader[0]);
            HttpQuery[] queries = Optional.ofNullable(annotation.queries()).orElseGet(() -> new HttpQuery[0]);

            // 设置 通用 请求头
            Map<String, String> headerMap = new HashMap<>();
            Arrays.stream(headers).forEach(header -> headerMap.put(header.name(), header.value()));
            methodConfig.config(COMMON_HEADER, headerMap);

            // 设置 通用 查询参数
            Map<String, String> queryMap = new HashMap<>();
            Arrays.stream(queries).forEach(query -> queryMap.put(query.name(), query.value()));
            methodConfig.config(COMMON_QUERY, queryMap);
        }
    }

    abstract class HttpProcessLifeCycle extends AbstractHttpProcessLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpMapping> {
        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, HttpMapping annotation) {
        }

        @Override
        public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            doBefore(data, methodConfig, req, res, args);
            RemoteNetNodeKey nodeKey = req.get(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY);
            RemoteNetClient client = RemoteNetClient.REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP.get(nodeKey);
            if (Objects.isNull(client)) {
                client = newRemoteNetClient(nodeKey);
                RemoteNetClient.REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP.put(nodeKey, client);
            }
            req.put(RemoteNetClient.REMOTE_NET_CLIENT_GENERIC_KEY, client);
            afterBefore(data, methodConfig, req, res, args);
        }

        protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            // do nothing here
        }

        protected void afterBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            // do nothing here
        }

        protected abstract RemoteNetClient newRemoteNetClient(RemoteNetNodeKey nodeKey);
    }
}