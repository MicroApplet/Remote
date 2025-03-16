/*
 *  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpQueryLifeCycle;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(HttpQuery.HttpQueryLifeCycle.class)
public @interface HttpQuery {
    String name();

    String value() default StringUtils.EMPTY;

    final class HttpQueryLifeCycle extends AbstractHttpQueryLifeCycle implements RemoteLifeCycle.LifeCycleHandler<HttpQuery> {

        @Override
        public void doInit(RemoteMethodConfig config, RemoteMethodParameter parameter, HttpQuery annotation) {
            Map<String, RemoteMethodParameter> queryConfig = config.config(HTTP_QUERY_CONFIG);
            if (Objects.isNull(queryConfig))
                queryConfig = new HashMap<>();

            String name = annotation.name();
            parameter.setDefaultValue(annotation.value());
            queryConfig.put(name,parameter);
            config.config(HTTP_QUERY_CONFIG, queryConfig);
        }
    }
}