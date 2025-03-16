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
package com.asialjim.microapplet.remote.http.annotation.body;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractFormDataLifeCycle;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            List<RemoteMethodParameter> parameters = Optional.ofNullable(methodConfig.config(FORM_DATA_CONFIG)).orElseGet(ArrayList::new);
            parameters.add(methodParameter);
            methodConfig.config(FORM_DATA_CONFIG, parameters);
        }
    }
}