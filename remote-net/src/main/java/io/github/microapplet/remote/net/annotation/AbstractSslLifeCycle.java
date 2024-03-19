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
 package io.github.microapplet.remote.net.annotation;

import io.github.microapplet.remote.context.*;
import io.github.microapplet.remote.lifecycle.callback.Before;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;

import javax.net.ssl.SSLContext;
import java.util.Objects;

 /**
  * SSL 基础处理器
  *
  * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
  * @version 4.0
  * @since 2023/7/10, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
  */
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