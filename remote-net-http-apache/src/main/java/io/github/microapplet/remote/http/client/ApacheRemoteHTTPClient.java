/*
 * Copyright 2014-2024 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
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

package io.github.microapplet.remote.http.client;

import io.github.microapplet.remote.context.*;
import io.github.microapplet.remote.http.annotation.HttpMethod;
import io.github.microapplet.remote.http.annotation.lifecycle.*;
import io.github.microapplet.remote.net.annotation.ServerLifeCycle;
import io.github.microapplet.remote.net.client.RemoteNetClient;
import io.github.microapplet.remote.net.constant.RemoteConstant;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.*;
import org.apache.http.conn.socket.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE;
import static io.github.microapplet.remote.http.annotation.lifecycle.BaseObjectMapperRequestBodyLifeCycle.STRING_BODY_KEY;

/**
 * 基于 Apache 的HTTP 客户端
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2024/3/8, &nbsp;&nbsp; <em>version:1.0.0</em>
 */
public class ApacheRemoteHTTPClient implements RemoteNetClient {
    public static final GenericKey<HttpEntity> HTTP_ENTITY_GENERIC_KEY = GenericKey.keyOf("apache_http_request_entity");
    private static final Logger log = LoggerFactory.getLogger(ApacheRemoteHTTPClient.class);
    private static final Map<Integer, PoolingHttpClientConnectionManager> POOLING_HTTP_CLIENT_CONNECTION_MANAGER_MAP = new ConcurrentHashMap<>();
    private final RemoteNetNodeKey nodeKey;
    private final String proxyHost;
    private final Integer proxyPort;
    private final int timeout;
    private final int nodeCode;

    public ApacheRemoteHTTPClient(RemoteNetNodeKey nodeKey) {
        String schema;
        if (Objects.isNull(nodeKey)) {
            this.nodeKey = null;
            this.proxyHost = StringUtils.EMPTY;
            this.proxyPort = 1080;
            this.timeout = 5000;
            this.nodeCode = 0;
            return;
        }

        this.nodeKey = nodeKey;
        schema = nodeKey.getSchema();
        this.proxyHost = nodeKey.getProxyHost();
        this.proxyPort = nodeKey.getProxyPort();
        this.timeout = nodeKey.getTimeout();
        SSLContext sslContext = nodeKey.getSslContext();
        int sslCode = -1;
        if (Objects.isNull(sslContext)) {
            if (StringUtils.equalsIgnoreCase(schema, "https")) {
                try {
                    sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
                } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                    throw new RuntimeException(e);
                }
                sslCode = 0;
            }
        } else sslCode = sslContext.hashCode();

        this.nodeCode = (schema + nodeKey.getHost() + nodeKey.getPort() + nodeKey.getProxyHost() + nodeKey.getProxyPort() + sslCode).hashCode();
        Registry<ConnectionSocketFactory> reg;
        PoolingHttpClientConnectionManager cm = POOLING_HTTP_CLIENT_CONNECTION_MANAGER_MAP.get(this.nodeCode);
        if (Objects.nonNull(cm))
            return;

        reg = this.nodeKey.proxyEnable()
                ? RegistryBuilder.<ConnectionSocketFactory>create().register(schema, Objects.nonNull(sslContext) ? new Socks5ProxySSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE) : new Socks5ProxyConnectionSocketFactory()).build()
                : RegistryBuilder.<ConnectionSocketFactory>create().register(schema, Objects.nonNull(sslContext) ? new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE) : PlainConnectionSocketFactory.getSocketFactory()).build();

        cm = new PoolingHttpClientConnectionManager(reg);
        POOLING_HTTP_CLIENT_CONNECTION_MANAGER_MAP.put(this.nodeCode, cm);
    }

    public static void addStringEntity(RemoteReqContext req) {
        String charsetName = Optional.ofNullable(req.get(RemoteConstant.CHARSET)).orElse(StandardCharsets.UTF_8.name());
        String bodyString = Optional.ofNullable(req.get(STRING_BODY_KEY)).orElse(StringUtils.EMPTY);
        StringEntity entity = new StringEntity(bodyString, charsetName);
        req.put(ApacheRemoteHTTPClient.HTTP_ENTITY_GENERIC_KEY, entity);
    }

    public static void addOctetStreamEntity(RemoteReqContext req) {
        Map<String, String> header = Optional.ofNullable(req.get(HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
        String contentType = header.entrySet().stream()
                .filter(entry -> StringUtils.equalsIgnoreCase("content-type", entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny().orElse("application/octet-stream");

        byte[] bytes = Optional.ofNullable(req.get(AbstractOctetStreamBodyLifeCycle.OCTET_STREAM_VALUE)).orElse(new byte[0]);
        ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create(contentType));
        req.put(ApacheRemoteHTTPClient.HTTP_ENTITY_GENERIC_KEY, entity);
    }

    private static void addHttpHeader(RemoteReqContext req, HttpRequest httpRequest) {
        Map<String, String> commonHeader = req.get(AbstractHttpMappingLifeCycle.COMMON_HEADER);
        Map<String, String> headers = req.get(HTTP_HEADER_VALUE);
        Map<String, String> targetHeader = new HashMap<>();
        if (MapUtils.isNotEmpty(headers))
            headers.forEach(targetHeader::putIfAbsent);
        if (MapUtils.isNotEmpty(commonHeader))
            commonHeader.forEach(targetHeader::putIfAbsent);

        targetHeader.forEach(httpRequest::addHeader);

    }

    public static String parseHttpUrl(RemoteReqContext req) {
        String uri = Optional.ofNullable(req.get(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI)).orElse(StringUtils.EMPTY);
        if (uri.startsWith("/"))
            uri = uri.replaceFirst("/", "");
        return String.format("%s://%s:%d/%s", req.get(RemoteConstant.SCHEMA), req.get(RemoteConstant.HOST), req.get(RemoteConstant.PORT), uri);
    }

    private static HttpUriRequest httpRequest(String method, String uri, HttpEntity entity) throws MethodNotSupportedException {
        switch (method) {
            case HttpMethod.GET:
                return new HttpGet(uri);

            case HttpMethod.HEAD:
                return new HttpHead(uri);

            case HttpMethod.OPTIONS:
                return new HttpOptions(uri);

            case HttpMethod.DELETE:
                return new HttpDelete(uri);

            case HttpMethod.TRACE:
                return new HttpTrace(uri);

            case HttpMethod.POST:
                HttpPost post = new HttpPost(uri);
                if (Objects.nonNull(entity))
                    post.setEntity(entity);
                return post;

            case HttpMethod.PUT:
                HttpPut httpPut = new HttpPut(uri);
                if (Objects.nonNull(entity))
                    httpPut.setEntity(entity);
                return httpPut;

            case HttpMethod.PATCH:
                HttpPatch patch = new HttpPatch(uri);
                if (Objects.nonNull(entity))
                    patch.setEntity(entity);
                return patch;
        }


        throw new MethodNotSupportedException(method + " method not supported");
    }

    @Override
    public void send(RemoteReqContext req, RemoteResContext res) {
        log.info("\r\n\tRemote NET Req Exec === Endpot: {}", this);
        PoolingHttpClientConnectionManager cm = POOLING_HTTP_CLIENT_CONNECTION_MANAGER_MAP.get(this.nodeCode);

        // 处理HTTP链接
        String uri = parseHttpUrl(req);

        CloseableHttpClient httpclient;
        if (this.nodeKey.proxyEnable()) {

            Credentials credentials = new UsernamePasswordCredentials("proxy_user", "proxy_pass");
            AuthScope authScope = new AuthScope(this.nodeKey.getHost(), this.nodeKey.getProxyPort());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(authScope, credentials);

            //noinspection resource
            httpclient = HttpClients.custom().setConnectionManager(cm).setDefaultCredentialsProvider(credsProvider).build();
        } else {
            //noinspection resource
            httpclient = HttpClients.custom().setConnectionManager(cm).build();
        }

        try {
            HttpClientContext context = HttpClientContext.create();
            RequestConfig.Builder custom = RequestConfig.custom();
            // 添加代理
            addSocksProxy(context);
            // 添加超时时间
            addRequestTimeout(custom);
            context.setRequestConfig(custom.build());

            HttpEntity entity = req.get(HTTP_ENTITY_GENERIC_KEY);
            String method = Optional.ofNullable(req.get(AbstractHttpMappingLifeCycle.HTTP_METHOD_KEY)).orElse(StringUtils.EMPTY);
            HttpUriRequest httpRequest = httpRequest(method, uri, entity);

            // 添加请求头
            addHttpHeader(req, httpRequest);
            HttpHost httpHost = new HttpHost(this.nodeKey.getHost(), this.nodeKey.getPort(), this.nodeKey.getSchema());
            try (CloseableHttpResponse response = httpclient.execute(httpHost, httpRequest, context)) {
                if (Objects.isNull(response))
                    return;

                res.setStatus(response.getStatusLine());
                Map<String, String> headerMap = new HashMap<>();
                Optional.ofNullable(response.getAllHeaders())
                        .map(Arrays::stream)
                        .ifPresent(stream ->
                                stream.forEach(item ->
                                        headerMap.putIfAbsent(item.getName(), item.getValue())));

                Optional.ofNullable(res.getHeaders())
                        .filter(item -> item instanceof Map<?, ?>)
                        .map(item -> (Map<?, ?>) item)
                        .ifPresent((Consumer<Map<?, ?>>) map ->
                                map.forEach((k, v)
                                        -> headerMap.putIfAbsent(String.valueOf(k), String.valueOf(v))));

                res.setHeaders(headerMap);
                ProtocolVersion protocolVersion = response.getProtocolVersion();
                res.setProtocol(protocolVersion);
                HttpEntity resEntity = response.getEntity();
                if (Objects.isNull(resEntity))
                    return;

                byte[] byteArray = IOUtils.toByteArray(resEntity.getContent());
                if (headerMap.keySet().stream().noneMatch(item -> StringUtils.equalsIgnoreCase("content-length", item)))
                    headerMap.put("Content-Length", String.valueOf(ArrayUtils.getLength(byteArray)));

                res.setTempData(byteArray);
            } catch (IOException e) {
                res.setCause(e);
            } finally {
                ServerLifeCycle.countDown(req);
            }
        } catch (MethodNotSupportedException e) {
            res.setCause(e);
        }
    }

    private void addRequestTimeout(RequestConfig.Builder custom) {
        custom.setConnectTimeout(this.timeout);
        custom.setSocketTimeout(this.timeout);
        custom.setConnectionRequestTimeout(this.timeout);
    }

    private void addSocksProxy(HttpClientContext context) {
        if (this.nodeKey.proxyEnable()) {
            InetSocketAddress socksAddr = new InetSocketAddress(proxyHost, proxyPort);
            context.setAttribute("socks.address", socksAddr);
        }
    }

    private static class Socks5ProxyConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) {
            InetSocketAddress socksAddr = (InetSocketAddress) context.getAttribute("socks.address");
            SocksProxy proxy = SocksProxy.create(socksAddr, 5);
            return new Socket(proxy);
        }
    }

    private static class Socks5ProxySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

        public Socks5ProxySSLConnectionSocketFactory(final SSLContext sslContext, HostnameVerifier hostnameVerifier) {
            super(sslContext, hostnameVerifier);
        }

        @Override
        public Socket createSocket(final HttpContext context) {
            InetSocketAddress socksAddr = (InetSocketAddress) context.getAttribute("socks.address");
            SocksProxy proxy = SocksProxy.create(socksAddr, 5);
            return new Socket(proxy);
        }
    }
}