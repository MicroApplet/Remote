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
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractFormDataLifeCycle;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文件上传处理
 * <br/>
 * 可选上传基本类型： {@link String}, {@link Array byte[]}, {@link File}, {@link InputStream}
 * <br/>
 * 可选上传复合类型： {@link Map Map-String-?}, {@link List List-?}
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/13, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(FormData.FormDataLifeCycle.class)
public @interface FormData {

    String name() default "";
    boolean attr() default false;
    String mimeType() default "";
    abstract class FormDataLifeCycle extends AbstractFormDataLifeCycle implements RemoteLifeCycle.LifeCycleHandler<FormData> {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, FormData annotation) {
            Class<?> clazz = methodParameter.getClazz();
            if (File.class.isAssignableFrom(clazz) || byte[].class.isAssignableFrom(clazz) || InputStream.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz)){
                List<RemoteMethodParameter> parameters = methodConfig.config(FORM_DATA_CONFIG);
                if (Objects.isNull(parameters)){
                    parameters = new ArrayList<>();
                }
                parameters.add(methodParameter);
                methodConfig.config(FORM_DATA_CONFIG, parameters);
            }
            else
                throw new IllegalStateException("Form Data Request Type: " + methodParameter.getName() + " must be File, byte[], InputStream or Map");
        }
    }
}