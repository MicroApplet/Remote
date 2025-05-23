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

import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.lifecycle.callback.After;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import com.asialjim.microapplet.remote.lifecycle.callback.Invoke;
import com.asialjim.microapplet.remote.net.annotation.Ssl;
import com.asialjim.microapplet.remote.net.client.RemoteNetClient;
import com.asialjim.microapplet.remote.net.constant.RemoteConstant;
import com.asialjim.microapplet.remote.net.response.ResWithHeader;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public abstract class AbstractHttpProcessLifeCycle implements Before, Invoke, After {
    private static final Logger log = LoggerFactory.getLogger(AbstractHttpProcessLifeCycle.class);

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        String bodyStr = Optional.ofNullable(req.get(BaseObjectMapperRequestBodyLifeCycle.STRING_BODY_KEY)).orElse(StringUtils.EMPTY);
        String url = req.get(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI);
        Map<String, String> headers = Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
        headers.putIfAbsent("User-Agent", "Remote HTTP Client on Java 8");
        headers.putIfAbsent("Host", req.get(RemoteConstant.HOST));
        req.put(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE, headers);

        log.info("Remote NET Req Line >>> Client:{} >>> {} {}://{}:{}{}", methodConfig.getRemoteName(), req.get(AbstractHttpMappingLifeCycle.HTTP_METHOD_KEY), req.get(RemoteConstant.SCHEMA), req.get(RemoteConstant.HOST), req.get(RemoteConstant.PORT), url);
        log.info("Remote NET Req Head >>> Client:{} >>> {}", methodConfig.getRemoteName(), headers);
        AbstractFormDataLifeCycle.callFromLog(req);
        Boolean fromDataRequest = req.get(AbstractFormDataLifeCycle.FORM_DATA_REQUEST);
        if (StringUtils.isNotBlank(bodyStr) && !Boolean.TRUE.equals(fromDataRequest))
            log.info("Remote NET Req Body >>> Client:{} >>> {}", methodConfig.getRemoteName(), bodyStr);
        SSLContext sslContext = req.get(Ssl.SslLifeCycle.SSL_CONTEXT_GENERIC_KEY);
        if (Objects.nonNull(sslContext))
            log.info("Remote NET Req Ssl  >>> Client:{} >>> {}", methodConfig.getRemoteName(), sslContext);

        RemoteNetClient client = req.get(RemoteNetClient.REMOTE_NET_CLIENT_GENERIC_KEY);
        if (Objects.nonNull(client))
            // 发送请求
            client.send(req, res);
    }

    @Override
    public void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        //noinspection unchecked
        Map<String, String> headers = (Map<String, String>) res.getHeaders();
        if (MapUtils.isEmpty(headers))
            return;

        Object resData = res.getData();
        if (!(resData instanceof ResWithHeader))
            return;

        ResWithHeader withHeader = (ResWithHeader) resData;
        headers.forEach(withHeader::addHeader);
    }
}