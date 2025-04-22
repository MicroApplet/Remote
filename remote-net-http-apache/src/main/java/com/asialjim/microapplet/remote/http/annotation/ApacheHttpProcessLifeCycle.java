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
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteSubProperty;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractHttpQueryLifeCycle;
import com.asialjim.microapplet.remote.http.client.ApacheRemoteHTTPClient;
import com.asialjim.microapplet.remote.net.annotation.AbstractSslLifeCycle;
import com.asialjim.microapplet.remote.net.annotation.ServerLifeCycle;
import com.asialjim.microapplet.remote.net.client.RemoteNetClient;
import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RemoteSubProperty("apache")
public final class ApacheHttpProcessLifeCycle extends HttpMapping.HttpProcessLifeCycle {

    @Override
    public void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        String uri = req.get(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI);
        if (StringUtils.isBlank(uri))
            uri = methodConfig.config(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI);

        Map<String, String> commonQuery = Optional.ofNullable(req.get(AbstractHttpMappingLifeCycle.COMMON_QUERY)).orElseGet(HashMap::new);
        Map<String, String> configCommonQuery = methodConfig.config(AbstractHttpMappingLifeCycle.COMMON_QUERY);
        Optional.ofNullable(configCommonQuery).ifPresent(item -> item.forEach(commonQuery::putIfAbsent));

        Map<String, String> queries = Optional.ofNullable(req.get(AbstractHttpQueryLifeCycle.HTTP_QUERY_VALUE)).orElseGet(HashMap::new);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        commonQuery.entrySet().stream().map(en -> new BasicNameValuePair(en.getKey(), en.getValue())).forEach(nameValuePairList::add);
        queries.entrySet().stream().map(en -> new BasicNameValuePair(en.getKey(), en.getValue())).forEach(nameValuePairList::add);

        String query = URLEncodedUtils.format(nameValuePairList, StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(query))
            uri += "&" + query;
        if (StringUtils.contains(uri, "&") && !StringUtils.contains(uri, "?"))
            uri = uri.replaceFirst("&", "?");

        req.put(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI, uri);
        Optional.ofNullable(req.get(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY))
                .ifPresent(item -> Optional.ofNullable(req.get(AbstractSslLifeCycle.SSL_CONTEXT_GENERIC_KEY))
                        .ifPresent(item::setSslContext));

        Map<String, String> headerMap = Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
        req.put(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE, headerMap);
    }

    @Override
    protected RemoteNetClient newRemoteNetClient(RemoteNetNodeKey nodeKey) {
        return new ApacheRemoteHTTPClient(nodeKey);
    }
}