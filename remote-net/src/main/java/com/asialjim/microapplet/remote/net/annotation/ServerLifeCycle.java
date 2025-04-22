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
import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.*;
import com.asialjim.microapplet.remote.net.constant.RemoteConstant;
import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;
import com.asialjim.microapplet.remote.net.exception.ConnectionException;
import com.asialjim.microapplet.remote.net.exception.DedicatedTimeoutException;
import com.asialjim.microapplet.remote.net.repository.ApiServerEnvironmentHolder;
import com.asialjim.microapplet.remote.net.repository.ApiServerInfo;
import com.asialjim.microapplet.remote.net.repository.ApiServerRepository;
import com.asialjim.microapplet.remote.net.repository.ApiServerRepositoryHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.ConnectException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.asialjim.microapplet.remote.net.annotation.ApiServerEnvironmentLifeCycle.NET_ENV_KEY;
import static com.asialjim.microapplet.remote.net.context.RemoteContext.REQUEST_SEND;

public final class ServerLifeCycle implements Before, After, SuccessWhen, OnError, Finally, RemoteLifeCycle.LifeCycleHandler<Server> {
    public static final GenericKey<RemoteNetNodeKey> NET_NODE_KEY_GENERIC_KEY = GenericKey.keyOf("REMOTE_NET_NODE_KEY");
    public static final GenericKey<CountDownLatch> ASYNC_COUNT_DOWN_LATCH_KEY = GenericKey.keyOf("ASYNC_COUNT_DOWN_LATCH");
    public static final String GLOBAL_NET_ENV = "NET";
    private static final Logger log = LoggerFactory.getLogger(ServerLifeCycle.class);

    public static void countDown(RemoteReqContext req) {
        req.put(REQUEST_SEND, Boolean.TRUE);
        CountDownLatch countDownLatch = req.get(ASYNC_COUNT_DOWN_LATCH_KEY);
        if (Objects.nonNull(countDownLatch))
            countDownLatch.countDown();
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE + 4;
    }

    @Override
    public void doInit(RemoteMethodConfig config, RemoteMethodParameter methodParameter, Server annotation) {
        config.config(RemoteConstant.SCHEMA, annotation.schema());          // 协议
        config.config(RemoteConstant.HOST, annotation.host());              // 主机名
        config.config(RemoteConstant.PORT, annotation.port());              // 端口
        config.config(RemoteConstant.PROXY_HOST, annotation.proxyHost());   // 代理主机名
        config.config(RemoteConstant.PROXY_PORT, annotation.proxyPort());   // 代理端口
        config.config(RemoteConstant.SUPPLIER, annotation.supplier());      // API 供应商
        config.config(RemoteConstant.NAMESPACE, annotation.namespace());    // API 业务空间
        config.config(RemoteConstant.ENV, annotation.env());                // API 环境
        config.config(RemoteConstant.TIMEOUT, annotation.timeout());        // 超时时间
        config.config(RemoteConstant.CHARSET, annotation.charset());        // 字符集
    }

    @Override
    public void before(Object data, RemoteMethodConfig config, RemoteReqContext req, RemoteResContext res, Object[] args) {
        //    通讯协议  主机名  代理主机名: 初始化获取全局代理主机名
        String schema, host, proxyHost = ApiServerEnvironmentHolder.globalProxyHost(), charset, logLevel = ApiServerEnvironmentHolder.globalLogLevel();

        //      端口  代理端口: 初始化获取全局代理主机端口                             超时时间
        Integer port, proxyPort = ApiServerEnvironmentHolder.globalProxyPort(), timeout;

        schema = config.config(RemoteConstant.SCHEMA);             // 通讯协议
        host = config.config(RemoteConstant.HOST);                 // 主机名
        port = config.config(RemoteConstant.PORT);                 // 端口

        // 方法签名配置代理服务器信息
        if (StringUtils.isNotBlank(config.config(RemoteConstant.PROXY_HOST))) {
            proxyHost = config.config(RemoteConstant.PROXY_HOST);  // 代理主机名
            proxyPort = config.config(RemoteConstant.PROXY_PORT);  // 代理端口
        }

        charset = config.config(RemoteConstant.CHARSET);           // 字符集
        timeout = config.config(RemoteConstant.TIMEOUT);           // 超时时间

        String supplier = config.config(RemoteConstant.SUPPLIER);              // API 供应商编号
        String namespace = config.config(RemoteConstant.NAMESPACE);            // API 业务编号
        String env = config.config(RemoteConstant.ENV);                        // API 环境编号
        if (req.containsKey(NET_ENV_KEY) && StringUtils.isNotBlank(req.get(NET_ENV_KEY)))            // 用户定制 API 环境编号
            env = req.get(NET_ENV_KEY);

        // 查询服务器信息
        ApiServerInfo server = finApiServerInfo(args, config, supplier, namespace, env);
        if (Objects.nonNull(server) && StringUtils.isNotBlank(server.getHost()) && !StringUtils.equals(ApiServerInfo.LOOP, server.getHost())) {
            schema = server.getSchema();
            host = server.getHost();
            port = server.getPort();
            charset = server.getCharset();
            timeout = server.getTimeout();
            // 服务器信息配置代理服务器信息
            if (StringUtils.isNotBlank(server.getProxyHost()) && Objects.nonNull(server.getProxyPort()) && server.getProxyPort() > 0) {
                proxyHost = server.getProxyHost();
                proxyPort = server.getProxyPort();
            }
        } else {
            log.info("使用默认服务器配置: {}://{}:{}", schema, host, port);
        }


        // 用户在线程上指定代理服务器
        if (StringUtils.isNotBlank(ApiServerEnvironmentHolder.localProxyHost()) && Objects.nonNull(ApiServerEnvironmentHolder.localProxyPort()) && ApiServerEnvironmentHolder.localProxyPort() > 0) {
            proxyHost = ApiServerEnvironmentHolder.localProxyHost();
            proxyPort = ApiServerEnvironmentHolder.localProxyPort();
            log.info("线程指定代理主机名：{}，主机端口：{}", proxyHost, proxyPort);
        }

        if (StringUtils.isNotBlank(ApiServerEnvironmentHolder.localLogLevel()))
            logLevel = ApiServerEnvironmentHolder.localLogLevel();

        if (Objects.isNull(timeout)) timeout = 5000;
        if (StringUtils.isBlank(schema))
            throw new IllegalStateException("Remote Net 客户端: " + config.getRemoteName() + "网络通讯协议为空");
        if (StringUtils.isBlank(host))
            throw new IllegalStateException("Remote Net 客户端: " + config.getRemoteName() + "网络通讯主机名为空");

        req.put(RemoteConstant.SCHEMA, schema);              // 设置协议
        req.put(RemoteConstant.HOST, host);                  // 设置主机名
        req.put(RemoteConstant.PROXY_HOST, proxyHost);       // 设置代理主机名
        req.put(RemoteConstant.PORT, port);                  // 设置端口
        req.put(RemoteConstant.PROXY_PORT, proxyPort);       // 设置代理端口
        req.put(RemoteConstant.CHARSET, charset);            // 设置字符集
        req.put(RemoteConstant.TIMEOUT, timeout);            // 设置超时时间

        req.put(RemoteConstant.SUPPLIER, supplier);
        req.put(RemoteConstant.NAMESPACE, namespace);
        req.put(RemoteConstant.ENV, env);

        RemoteNetNodeKey nodeKey = RemoteNetNodeKey.builder().schema(schema).host(host).port(port).proxyHost(proxyHost).proxyPort(proxyPort).trace(MDC.get("REQUEST_ID")).logLevel(logLevel).timeout(timeout).build();
        req.put(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY, nodeKey);

        req.put(ASYNC_COUNT_DOWN_LATCH_KEY, new CountDownLatch(1));
    }

    @Override
    public void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        Integer timeout = methodConfig.config(RemoteConstant.TIMEOUT);
        Boolean send = req.get(REQUEST_SEND);
        if (Boolean.TRUE.equals(send)) {
            CountDownLatch countDownLatch = req.get(ASYNC_COUNT_DOWN_LATCH_KEY);
            if (Objects.isNull(countDownLatch))
                return;

            try {
                boolean await = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
                if (!await)
                    res.setCause(new TimeoutException("等待超时，最大允许超时时间：" + timeout + "毫秒"));
            } catch (InterruptedException e) {
                res.setCause(new TimeoutException("等待超时，最大允许超时时间：" + timeout + "毫秒"));
            }
        }
    }

    @Override
    public boolean onError(Object data, RemoteMethodConfig config, RemoteReqContext req, RemoteResContext res, Throwable ex, Object[] args) {
        log.info("\r\n\tRemote NET Req Err  === Client:{} === {}", config.getRemoteName(), ex.getMessage(), ex);

        // 连接异常
        if (ex instanceof ConnectionException) {
            // --------------------------------------------------专线超时后不做处理------------------------------------------------
            DedicatedTimeoutException dedicatedTimeoutException = DedicatedTimeoutException.create(config.config(RemoteConstant.SUPPLIER), config.config(RemoteConstant.NAMESPACE), req.get(RemoteConstant.ENV), ex);
            // --------------------------------------------------专线超时后不做处理------------------------------------------------
            // 专线超时，记录超时次数
            if (Objects.nonNull(dedicatedTimeoutException)) {
                ApiServerRepositoryHolder.addTimeout(dedicatedTimeoutException);
                return true;
            }
        }

        // 超时重试
        if (ex instanceof TimeoutException)
            return true;

        return ex instanceof ConnectException;
    }

    @Override
    public boolean success(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        return true;
    }

    private ApiServerInfo finApiServerInfo(Object[] args, RemoteMethodConfig config, String supplier, String namespace, String env) {
        // 优先用函数式接口查询服务器信息，用户可以以参数的形式，将服务器信息仓库传入
        ApiServerInfo server = queryServerByFunctionalInterface(args, config, supplier, namespace, env);

        // 查询不到，则从系统中获取服务器信息
        if (Objects.isNull(server) && ApiServerRepositoryHolder.hasRepositories())
            server = ApiServerRepositoryHolder.get(supplier, namespace, env);
        return server;
    }

    private ApiServerInfo queryServerByFunctionalInterface(Object[] args, RemoteMethodConfig config, String supplier, String namespace, String env) {
        RemoteHandlerContext remoteHandlerContext = config.getRemoteHandlerContext();
        List<Integer> callBackIndexes = remoteHandlerContext.callBackIndex();
        for (Integer index : callBackIndexes) {
            if (index < 0 || index > args.length)
                continue;
            Object arg = args[index];
            if ((Objects.isNull(arg)) || !(arg instanceof ApiServerRepository))
                continue;

            ApiServerInfo server = ((ApiServerRepository) arg).queryNetServerInfoBySupplierAndNamespaceAndEnv(supplier, namespace, env);
            if (Objects.nonNull(server)) {
                log.info("从函数式接口：{} 中获取到服务器信息：{}", arg, server);
                return server;
            }
        }
        return null;
    }

    @Override
    public void finallyFun(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        req.clean();
    }
}