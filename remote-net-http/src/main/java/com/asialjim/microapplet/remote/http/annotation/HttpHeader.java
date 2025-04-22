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
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(HttpHeader.HttpHeaderLifeCycle.class)
public @interface HttpHeader {
    String name() default StringUtils.EMPTY;

    String value() default StringUtils.EMPTY;

     class HttpHeaderLifeCycle extends AbstractHttpHeaderLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpHeader> {

        @Override
        public void doInit(RemoteMethodConfig config, RemoteMethodParameter parameter, HttpHeader annotation) {
            Class<?> clazz = parameter.getClazz();
            if (!String.class.isAssignableFrom(clazz) && !clazz.isAssignableFrom(Map.class))
                throw new IllegalArgumentException("Http Header Parameter Class of " + config.getRemoteName() + "@" + parameter.getName() + " must be String");

            Map<String, RemoteMethodParameter> httpHeaderConfig = config.config(HTTP_HEADER_CONFIG);
            if (Objects.isNull(httpHeaderConfig))
                httpHeaderConfig = new HashMap<>();

            parameter.setDefaultValue(annotation.value());

            String name = annotation.name();
            if (StringUtils.isBlank(name))
                name = EMPTY_HEADER_KEY_FOR_MAP;
            httpHeaderConfig.put(name, parameter);

            Map<String, String> httpHeaderValue = config.config(HTTP_HEADER_VALUE);
            if (Objects.isNull(httpHeaderValue))
                httpHeaderValue = new HashMap<>();

            if (StringUtils.equalsIgnoreCase(EMPTY_HEADER_KEY_FOR_MAP,name))
                httpHeaderValue.put(name, annotation.value());

            config.config(HTTP_HEADER_CONFIG, httpHeaderConfig);
            config.config(HTTP_HEADER_VALUE, httpHeaderValue);
        }
    }
}