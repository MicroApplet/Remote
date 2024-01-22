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
package io.github.microapplet.remote.http.annotation.body;

import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteMethodParameter;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractJsonBodyLifeCycle;

import java.lang.annotation.*;

/**
 * 发送 JSON 请求数据
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/13, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version: 8</em>
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(JsonBody.JsonBodyLifeCycle.class)
public @interface JsonBody {

    abstract class JsonBodyLifeCycle extends AbstractJsonBodyLifeCycle implements RemoteLifeCycle.LifeCycleHandler<JsonBody> {
        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, JsonBody annotation) {
            methodConfig.config(JSON_BODY_KEY, methodParameter);
        }
    }
}