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
package io.github.microapplet.remote.http.annotation;

import io.github.microapplet.remote.annotation.RemoteSubProperty;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.http.annotation.body.XmlBody;

import static io.github.microapplet.remote.http.client.ApacheRemoteHTTPClient.addStringEntity;

/**
 * Apache HTTP XML 请求体处理器
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/10, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@RemoteSubProperty("apache")
public final class ApacheXmlBodyLifeCycle extends XmlBody.XmlBodyLifeCycle {
    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        addStringEntity(req);
    }
}