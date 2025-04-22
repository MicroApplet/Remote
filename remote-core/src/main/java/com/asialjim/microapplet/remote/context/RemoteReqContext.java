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
package com.asialjim.microapplet.remote.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;


@ToString
public final class RemoteReqContext {
    private final static Logger log = LoggerFactory.getLogger(RemoteReqContext.class);
    /**
     * 重试次数
     */
    private transient int retryTimes = 0;

    /**
     * 请求配置
     */
    @Setter
    @Getter
    private transient RemoteGenericMap requestContext;

    /**
     * 获取重试次数
     */
    public int retryTimes() {
        return this.retryTimes;
    }

    /**
     * 添加重试次数
     */
    public void addRetryTimes() {
        retryTimes++;
    }

    public <Value> void put(GenericKey<Value> configKey, Value configValue) {
        if (Objects.isNull(configKey) || Objects.isNull(configValue))
            return;

        if (Objects.isNull(this.requestContext))
            this.requestContext = new RemoteGenericMap();

        this.requestContext.valueOf(configKey, configValue);
    }

    @SuppressWarnings("unused")
    public Collection<Object> values(){
        return Optional.ofNullable(this.getRequestContext()).map(RemoteGenericMap::values).orElse(Collections.emptyList());
    }

    public <Value> Value get(GenericKey<Value> configKey) {
        if (Objects.isNull(configKey)) {
            log.warn("Config Key is null");
            return null;
        }
        if (Objects.isNull(this.requestContext))
            this.requestContext = new RemoteGenericMap();

        return this.requestContext.valueOf(configKey);
    }

    public <Value> boolean containsKey(GenericKey<Value> key) {
        if (Objects.isNull(key)) {
            log.warn("Config Key is null");
            return false;
        }
        if (Objects.isNull(this.requestContext))
            this.requestContext = new RemoteGenericMap();

        return this.requestContext.containsKey(key);
    }

    public void clean() {
        if (Objects.isNull(this.requestContext))
            return;

        this.requestContext.clear();
    }
}