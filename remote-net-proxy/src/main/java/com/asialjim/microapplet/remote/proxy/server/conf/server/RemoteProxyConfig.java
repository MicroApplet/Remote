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
package com.asialjim.microapplet.remote.proxy.server.conf.server;


import com.asialjim.microapplet.remote.proxy.server.SocksServer;
import com.asialjim.microapplet.remote.proxy.server.conf.property.RemoteAutoConfig;
import com.asialjim.microapplet.remote.proxy.server.conf.property.RemoteProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@AutoConfigureAfter(RemoteAutoConfig.class)
public class RemoteProxyConfig implements InitializingBean {
    private RemoteProperty property;

    @Bean
    public SocksServer socksServer() {
        SocksServer server = new SocksServer();
        server.setBoss(property.getBoss());
        server.setWorker(property.getWorker());
        server.setPort(property.getPort());
        server.setLogLevel(property.getLogLevel());
        return server;
    }

    @Bean
    public RemoteHealthCheckServer healthCheckServer() {
        RemoteHealthCheckServer server = new RemoteHealthCheckServer();
        server.setHealthPort(property.getHealthPort());
        return server;
    }

    @Override
    public void afterPropertiesSet() {
        if (Objects.isNull(property))
            property = RemoteProperty.defaultRemoteProperty();

        log.info("\r\t开始启动前置代理服务器，配置: \r" +
                        "\t\tBoss 线程数：{}，\r" +
                        "\t\tWorker 线程数: {}，\r" +
                        "\t\t健康检查端口：{}， \r" +
                        "\t\t日志级别：{}， \r" +
                        "\t\t端口：{}",
                property.getBoss(),
                property.getWorker(),
                property.getHealthPort(),
                property.getLogLevel(),
                property.getPort());
    }

    @Autowired(required = false)
    public void setProperty(RemoteProperty property) {
        this.property = property;
    }
}