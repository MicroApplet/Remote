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
package io.github.microapplet.remote.net.repository;

import org.apache.commons.lang3.StringUtils;

/**
 * 网络环境配置
 *
 * @author Copyright  ©  <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 4.0
 * @since 2023/5/15, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@SuppressWarnings("unused")
public class ApiServerEnvironmentHolder {
    private static final ThreadLocal<String> ENV = new ThreadLocal<>();
    private static final ThreadLocal<String> PROXY_HOST = new ThreadLocal<>();
    private static final ThreadLocal<Integer> PROXY_PORT = new ThreadLocal<>();
    private static final ThreadLocal<String> LOG_LEVEL = new ThreadLocal<>();


    private static volatile String proxyHost;
    private static volatile Integer proxyPort;
    private static volatile String logLevel;

    public static String localLogLevel() {
        return LOG_LEVEL.get();
    }

    public static void localLogLevel(String logLevel) {
        LOG_LEVEL.set(logLevel);
    }

    public static String globalLogLevel() {
        return logLevel;
    }

    public static void globalLogLevel(String logLevel) {
        ApiServerEnvironmentHolder.logLevel = logLevel;
    }

    public static void localProxyHost(String proxyHost) {
        PROXY_HOST.set(proxyHost);
    }

    public static void localProxyPort(Integer proxyPort) {
        PROXY_PORT.set(proxyPort);
    }

    public static void globalProxyHost(String proxyHost) {
        ApiServerEnvironmentHolder.proxyHost = proxyHost;
    }

    public static void globalProxyPort(Integer proxyPort) {
        ApiServerEnvironmentHolder.proxyPort = proxyPort;
    }

    public static String localProxyHost() {
        return PROXY_HOST.get();
    }

    public static Integer localProxyPort() {
        return PROXY_PORT.get();
    }

    public static String globalProxyHost() {
        return ApiServerEnvironmentHolder.proxyHost;
    }

    public static Integer globalProxyPort() {
        return ApiServerEnvironmentHolder.proxyPort;
    }

    /**
     * 设置网络环境
     *
     * @since 2023/5/16
     */
    public static void set(String env) {
        if (StringUtils.isNotBlank(env))
            ENV.set(env);
    }

    /**
     * 清除当前线程网络环境
     *
     * @since 2023/5/16
     */
    public static void clean() {
        ENV.remove();
        PROXY_HOST.remove();
        PROXY_PORT.remove();
        LOG_LEVEL.remove();
    }

    /**
     * 获取网络环境
     *
     * @since 2023/5/16
     */
    public static String get() {
        return ENV.get();
    }
}