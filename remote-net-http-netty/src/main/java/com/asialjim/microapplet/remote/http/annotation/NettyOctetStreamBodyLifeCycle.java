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
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.Primary;
import com.asialjim.microapplet.remote.annotation.RemoteSubProperty;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.http.annotation.body.OctetStreamBody;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractOctetStreamBodyLifeCycle;
import com.asialjim.microapplet.remote.http.client.NettyRemoteHTTPClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Optional;

@Primary
@RemoteSubProperty("netty")
public final class NettyOctetStreamBodyLifeCycle extends OctetStreamBody.OctetStreamBodyLifeCycle {
    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        byte[] bytes = Optional.ofNullable(req.get(AbstractOctetStreamBodyLifeCycle.OCTET_STREAM_VALUE)).orElse(new byte[0]);
        ByteBuf content = Unpooled.wrappedBuffer(bytes);
        req.put(NettyRemoteHTTPClient.HTTP_REQUEST_BODY_KEY, content);
    }
}