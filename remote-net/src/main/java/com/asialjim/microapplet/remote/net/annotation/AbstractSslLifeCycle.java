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
 package com.asialjim.microapplet.remote.net.annotation;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;

import javax.net.ssl.SSLContext;
import java.util.Objects;

 public abstract class AbstractSslLifeCycle implements Before {
     public static final GenericKey<SSLContext> SSL_CONTEXT_GENERIC_KEY = GenericKey.keyOf("SSL_CONTEXT_KEY");
     public static final GenericKey<RemoteMethodParameter> SSL_PARAMETER_KEY = GenericKey.keyOf("SSL_PARAMETER_KEY");

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         RemoteMethodParameter parameter = methodConfig.config(SSL_PARAMETER_KEY);
         Object body = args[parameter.getIndex()];
         if (!(body instanceof SSLContext))
             return;
         SSLContext sslContext = (SSLContext) body;
         req.put(SSL_CONTEXT_GENERIC_KEY, sslContext);
         RemoteNetNodeKey nodeKey = req.get(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY);
         if (Objects.nonNull(nodeKey) && Objects.isNull(nodeKey.getSslContext()))
             nodeKey.setSslContext(sslContext);
     }
 }