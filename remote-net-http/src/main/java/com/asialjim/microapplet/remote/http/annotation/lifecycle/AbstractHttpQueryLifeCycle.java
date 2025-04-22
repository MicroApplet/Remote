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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


 public abstract class AbstractHttpQueryLifeCycle implements Before {
     public static final GenericKey<Map<String, RemoteMethodParameter>> HTTP_QUERY_CONFIG = GenericKey.keyOf("HTTP_QUERY_CONFIG");
     public static final GenericKey<Map<String, String>> HTTP_QUERY_VALUE = GenericKey.keyOf("HTTP_QUERY_VALUE");
     public static final GenericKey<Boolean> HTTP_QUERY_ADDED = GenericKey.keyOf("HTTP_QUERY_ADDED");

     @Override
     public final int order() {
         return Integer.MIN_VALUE + 200;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         Boolean aBoolean = req.get(HTTP_QUERY_ADDED);
         if (Boolean.TRUE.equals(aBoolean))
             return;

         Map<String, RemoteMethodParameter> queryConfig = methodConfig.config(HTTP_QUERY_CONFIG);
         if (MapUtils.isEmpty(queryConfig))
             return;

         Map<String, String> queries = Optional.ofNullable(req.get(HTTP_QUERY_VALUE)).orElseGet(HashMap::new);
         for (Map.Entry<String, RemoteMethodParameter> entry : queryConfig.entrySet()) {
             String name = entry.getKey();
             RemoteMethodParameter parameter = entry.getValue();
             String defaultValue = (String) parameter.getDefaultValue();
             String value = (String) args[parameter.getIndex()];
             if (StringUtils.isNotBlank(value))
                 queries.put(name, value);
             else if (StringUtils.isNotBlank(defaultValue))
                 queries.put(name, defaultValue);
         }

         doBefore(data, methodConfig, req, res, args);
         req.put(HTTP_QUERY_VALUE, queries);
         req.put(HTTP_QUERY_ADDED, Boolean.TRUE);
     }

     @SuppressWarnings("unused")
     protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {

     }
 }