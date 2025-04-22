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
package com.asialjim.microapplet.remote.proxy.server.conf.property;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Objects;

@Data
@ConfigurationProperties(prefix = RemoteProperty.PREFIX)
public class RemoteProperty implements Serializable {
    public static final String PREFIX = "remote.proxy";
    public static final String ENABLE = "enable";
    private static final Logger log = LoggerFactory.getLogger(RemoteProperty.class);
    private static final long serialVersionUID = 8814362388932400490L;
    private Boolean enable;
    private String boss;
    private String worker;
    private String port;
    private String healthPort;
    private String logLevel;

    public static RemoteProperty defaultRemoteProperty() {
        RemoteProperty brokerProperty = new RemoteProperty();
        brokerProperty.setBoss("1");
        brokerProperty.setWorker("0");
        brokerProperty.setHealthPort("13002");
        brokerProperty.setLogLevel("");
        brokerProperty.setPort("13001");
        log.info("未设置代理服务器参数，使用默认配置");
        return brokerProperty;
    }

    public Integer getPort() {
        Integer port;
        try {
            port = Integer.parseInt(this.port);
        } catch (Throwable t){
            port = 13001;
        }
        if (port.compareTo(1000) < 0)
            throw new IllegalStateException("未设置代理服务器端口，或者端口号小于 1000");
        return port;
    }

    public Integer getHealthPort(){
        int healthPort;
        try {
            healthPort = Integer.parseInt(this.healthPort);
        }catch (Throwable t){
            healthPort = 13002;
        }

        if (healthPort < 0)
            throw new IllegalStateException("未设置代理服务器健康检查端口，或者端口号小于 1000");
        if (Objects.equals(healthPort, getPort()))
            throw new IllegalStateException("代理端口和健康检查端口不能相同");
        return healthPort;
    }

    public Integer getBoss() {
        if (Objects.isNull(this.boss))
            return 1;

        try {
            return Integer.parseInt(this.boss);
        } catch (Throwable t){
            return 1;
        }
    }

    public Integer getWorker() {
        if (Objects.isNull(this.worker))
            return 0;

        try {
            return Integer.parseInt(this.worker);
        } catch (Throwable t){
            return 0;
        }

    }
}