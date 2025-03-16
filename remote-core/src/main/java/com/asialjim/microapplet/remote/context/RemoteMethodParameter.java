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
package com.asialjim.microapplet.remote.context;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class RemoteMethodParameter {
    private transient final String name;
    private transient final Type type;
    private transient final Class<?> clazz;
    private transient final int index;
    private transient final Parameter parameter;
    private transient final List<Annotation> parameterAnnotations;
    private transient Object defaultValue;
    private transient String contentType;

    private RemoteMethodParameter(String name, Type type, Class<?> clazz, int index, Parameter parameter){
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.index = index;
        this.parameter = parameter;
        this.parameterAnnotations = new ArrayList<>();
        Annotation[] annotations = parameter.getAnnotations();
        if (Objects.nonNull(annotations) && annotations.length > 0)
            this.parameterAnnotations.addAll(Arrays.asList(annotations));
    }

    public static RemoteMethodParameter create(int index, Parameter parameter) {
        if (Objects.isNull(parameter) || index < 0)
            return null;
        return new RemoteMethodParameter(parameter.getName(), parameter.getParameterizedType(), parameter.getType(), index, parameter);
    }
}
