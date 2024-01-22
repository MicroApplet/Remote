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
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpUriValueLifeCycle;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP uri 参数 *
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2022/12/22, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Documented
@SuppressWarnings("unused")
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(HttpUriValue.HttpUriValueLifeCycle.class)
public @interface HttpUriValue {

    /**
     * uri 参数名
     */
    String value();

    final class HttpUriValueLifeCycle extends AbstractHttpUriValueLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpUriValue> {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, HttpUriValue annotation) {
            Map<String, RemoteMethodParameter> config = methodConfig.config(HTTP_URI_CONFIG);
            if (Objects.isNull(config))
                config = new HashMap<>();

            String value = annotation.value();
            if (StringUtils.isBlank(value))
                throw new IllegalArgumentException("Http Request Url Path Value: " + methodParameter.getName() + " cannot be blank");

            if (!String.class.isAssignableFrom(methodParameter.getClazz()))
                throw new IllegalArgumentException("Http Request Url Path Value: " + methodParameter.getName() + " Type must be String");

            config.put(value, methodParameter);
            methodConfig.config(HTTP_URI_CONFIG, config);
        }
    }
}