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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
public class RemoteLocalEnvConfiguration implements ApplicationContextAware, InitializingBean {
    @Setter
    private ApplicationContext applicationContext;
    private RemoteLocalEnvProperty property;

    @Autowired(required = false)
    public void setProperty(RemoteLocalEnvProperty property) {
        this.property = property;
    }

    @Override
    public void afterPropertiesSet() {
        if (Objects.isNull(this.property)) {
            this.property = new RemoteLocalEnvProperty();
        }

        if (Objects.nonNull(this.property.getEnable())
                || Objects.nonNull(this.property.getEnv())
                || Objects.nonNull(this.property.getArch())
                || Objects.nonNull(this.property.getPrimaries()))
            return;

        Environment environment = applicationContext.getEnvironment();
        String enable = environment.getProperty("remote.local.enable");
        String env = environment.getProperty("remote.local.env");
        String arch = environment.getProperty("remote.local.arch");
        String primaries = environment.getProperty("remote.local.primaries");
        this.property.setEnable(Boolean.valueOf(enable));
        this.property.setEnv(RemoteLocalEnvironment.RemoteLocalEnv.nameOf(env));
        this.property.setArch(RemoteLocalEnvironment.Arch.nameOf(arch));
        this.property.setPrimaries(primaries);
    }

    @Bean
    @ConditionalOnMissingBean
    public RemoteLocalEnvironment localEnvironment() {
        log.info("Remote 本地环境：{}", property);
        return new RemoteLocalEnvironment() {

            @Override
            public RemoteLocalEnv doLocalEnv() {
                return Optional.ofNullable(property).map(RemoteLocalEnvProperty::getEnv).orElse(RemoteLocalEnv.DEFT);
            }

            @Override
            public Arch arch() {
                return Optional.ofNullable(property).map(RemoteLocalEnvProperty::getArch).orElse(Arch.X86);
            }
        };
    }

}