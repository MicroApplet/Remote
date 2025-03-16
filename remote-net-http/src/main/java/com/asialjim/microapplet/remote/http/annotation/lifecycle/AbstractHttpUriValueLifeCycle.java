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
 package com.asialjim.microapplet.remote.http.annotation.lifecycle;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

import static com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI;

 public abstract class AbstractHttpUriValueLifeCycle implements Before {
     public static final GenericKey<Map<String, RemoteMethodParameter>> HTTP_URI_CONFIG = GenericKey.keyOf("HTTP_URI_CONFIG_VALUE");

     @Override
     public int order() {
         return Integer.MIN_VALUE;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         String uri = methodConfig.config(HTTP_REQUEST_URI);

         Map<String, RemoteMethodParameter> uriConfig = methodConfig.config(HTTP_URI_CONFIG);
         if (MapUtils.isEmpty(uriConfig))
             return;

         for (Map.Entry<String, RemoteMethodParameter> entry : uriConfig.entrySet()) {
             RemoteMethodParameter parameter = entry.getValue();
             int index = parameter.getIndex();
             String value = (String) args[index];
             uri = uri.replaceAll("\\$\\{" + entry.getKey() + "}", value);
             uri = uri.replaceAll("#\\{" + entry.getKey() + "}", value);
         }

         req.put(HTTP_REQUEST_URI, uri);
     }
 }