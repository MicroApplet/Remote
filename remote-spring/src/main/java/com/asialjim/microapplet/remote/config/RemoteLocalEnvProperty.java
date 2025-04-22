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
package com.asialjim.microapplet.remote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Remote 本地环境配置属性
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.1
 * @since 2024/2/1, &nbsp;&nbsp; <em>version:1.0.1</em>
 */
@Configuration
@ConfigurationProperties(prefix = RemoteLocalEnvProperty.PREFIX)
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class RemoteLocalEnvProperty {
    public static final String PREFIX = "remote.local";

    private Boolean enable;
    private RemoteLocalEnvironment.RemoteLocalEnv env;
    private RemoteLocalEnvironment.Arch arch;
    private String primaries;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public RemoteLocalEnvironment.RemoteLocalEnv getEnv() {
        return env;
    }

    public void setEnv(RemoteLocalEnvironment.RemoteLocalEnv env) {
        this.env = env;
    }

    public RemoteLocalEnvironment.Arch getArch() {
        return arch;
    }

    public void setArch(RemoteLocalEnvironment.Arch arch) {
        this.arch = arch;
    }

    public String getPrimaries() {
        return primaries;
    }

    public void setPrimaries(String primaries) {
        this.primaries = primaries;
    }
}