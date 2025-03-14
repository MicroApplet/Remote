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
package io.github.microapplet.remote.http.annotation.lifecycle;

import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.lifecycle.callback.After;
import io.github.microapplet.remote.lifecycle.callback.Before;
import io.github.microapplet.remote.lifecycle.callback.Invoke;
import io.github.microapplet.remote.net.annotation.Ssl;
import io.github.microapplet.remote.net.client.RemoteNetClient;
import io.github.microapplet.remote.net.constant.RemoteConstant;
import io.github.microapplet.remote.net.response.ResWithHeader;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * HTTP 协议基础处理器
 *
 * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 4.0
 * @since 2023/7/10, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
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

        log.info("\r\n\tRemote NET Req Line >>> Client:{} >>> {} {}://{}:{}{}", methodConfig.getRemoteName(), req.get(AbstractHttpMappingLifeCycle.HTTP_METHOD_KEY), req.get(RemoteConstant.SCHEMA), req.get(RemoteConstant.HOST), req.get(RemoteConstant.PORT), url);
        log.info("\r\n\tRemote NET Req Head >>> Client:{} >>> {}", methodConfig.getRemoteName(), headers);
        AbstractFormDataLifeCycle.callFromLog(req);
        Boolean fromDataRequest = req.get(AbstractFormDataLifeCycle.FORM_DATA_REQUEST);
        if (StringUtils.isNotBlank(bodyStr) && !Boolean.TRUE.equals(fromDataRequest))
            log.info("\r\n\tRemote NET Req Body >>> Client:{} >>> {}", methodConfig.getRemoteName(), bodyStr);
        SSLContext sslContext = req.get(Ssl.SslLifeCycle.SSL_CONTEXT_GENERIC_KEY);
        if (Objects.nonNull(sslContext))
            log.info("\r\n\tRemote NET Req Ssl  >>> Client:{} >>> {}", methodConfig.getRemoteName(), sslContext);

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