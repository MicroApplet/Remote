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
 package com.asialjim.microapplet.remote.http.annotation.lifecycle;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

 public abstract class AbstractHttpHeaderLifeCycle implements Before {
     public static final GenericKey<Map<String, RemoteMethodParameter>> HTTP_HEADER_CONFIG = GenericKey.keyOf("HTTP_HEADER_CONFIG");
     public static final GenericKey<Map<String, String>> HTTP_HEADER_VALUE = GenericKey.keyOf("HTTP_HEADER_VALUE");
     protected static final String EMPTY_HEADER_KEY_FOR_MAP = "_mapHeader";


     @Override
     public int order() {
         return Integer.MIN_VALUE + 20;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         Map<String, String> headers = Optional.ofNullable(req.get(HTTP_HEADER_VALUE)).orElse(new HashMap<>());
         Map<String, RemoteMethodParameter> headerConfig = Optional.ofNullable(methodConfig.config(HTTP_HEADER_CONFIG)).orElse(new HashMap<>());
         headerConfig.forEach((key, value) -> {
             if (StringUtils.equalsIgnoreCase(key, EMPTY_HEADER_KEY_FOR_MAP)) {
                 try {
                     Object arg = args[value.getIndex()];
                     if (Objects.nonNull(arg) && arg instanceof Map) {
                         //noinspection unchecked
                         Map<String, String> headerMap = (Map<String, String>) arg;
                         headers.putAll(headerMap);
                     }
                 } catch (Throwable ignored) {

                 }
             } else {
                 String defaultValue = (String) value.getDefaultValue();
                 String headerValue = (String) args[value.getIndex()];
                 String targetValue = StringUtils.isNotBlank(headerValue) ? headerValue : defaultValue;
                 headers.put(key, targetValue);
             }
         });

         req.put(HTTP_HEADER_VALUE, headers);
     }
 }