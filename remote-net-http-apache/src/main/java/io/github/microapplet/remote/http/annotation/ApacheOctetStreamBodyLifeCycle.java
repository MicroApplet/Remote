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
import io.github.microapplet.remote.http.annotation.body.OctetStreamBody;
import io.github.microapplet.remote.http.client.ApacheRemoteHTTPClient;

/**
 * 基于Apache 的纯二进制 HTTP 请求体
 *
 * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/19, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@RemoteSubProperty("apache.http,https")
public final class ApacheOctetStreamBodyLifeCycle extends OctetStreamBody.OctetStreamBodyLifeCycle {
    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        ApacheRemoteHTTPClient.addOctetStreamEntity(req);
    }
}
