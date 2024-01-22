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

import io.github.microapplet.remote.context.GenericKey;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle;
import io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpQueryLifeCycle;
import io.github.microapplet.remote.http.pool.RemoteHttpClientPoolOnNetty;
import io.github.microapplet.remote.net.annotation.ServerLifeCycle;
import io.github.microapplet.remote.net.client.RemoteNetClient;
import io.github.microapplet.remote.net.constant.RemoteConstant;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;
import io.github.microapplet.remote.net.netty.NettyPoolUtil;
import io.github.microapplet.remote.net.netty.context.RemoteNettyChannelContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.microapplet.remote.http.annotation.lifecycle.AbstractFormDataLifeCycle.FORM_DATA_REQUEST;
import static io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle.COMMON_QUERY;
import static io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI;
import static io.github.microapplet.remote.http.annotation.lifecycle.BaseObjectMapperRequestBodyLifeCycle.STRING_BODY_KEY;
import static io.github.microapplet.remote.net.context.RemoteContext.REQUEST_SEND;

/**
 * 基于 Netty 的 HTTP 客户端
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/11, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@AllArgsConstructor
public final class NettyRemoteHTTPClient implements RemoteNetClient {
    public static final GenericKey<QueryStringEncoder> QUERY_STRING_ENCODER_GENERIC_KEY = GenericKey.keyOf("QUERY_STRING_ENCODER_KEY");
    public static final GenericKey<HttpRequest> HTTP_REQUEST_GENERIC_KEY = GenericKey.keyOf("NETTY_HTTP_REQUEST_KEY");
    public static final GenericKey<ByteBuf> HTTP_REQUEST_BODY_KEY = GenericKey.keyOf("NETTY_HTTP_REQUEST_BODY_KEY");
    public static final GenericKey<HttpPostRequestEncoder> POST_REQUEST_ENCODER_GENERIC_KEY = GenericKey.keyOf("NETTY_HTTP_POST_ENCODER_KEY");
    public static final GenericKey<Promise<FullHttpResponse>> FULL_HTTP_RESPONSE_PROMISE_KEY = GenericKey.keyOf("FULL_HTTP_RESPONSE_PROMISE_KEY");
    public static final GenericKey<SimpleChannelPool> CHANNEL_POOL_GENERIC_KEY = GenericKey.keyOf("CHANNEL_POOL_GENERIC_KEY");
    private static final Logger log = LoggerFactory.getLogger(NettyRemoteHTTPClient.class);
    private final RemoteNetNodeKey nodeKey;

    public static HttpRequest buildHttpRequest(RemoteMethodConfig methodConfig, RemoteReqContext req) {
        if (Objects.nonNull(req.get(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY)))
            return req.get(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY);
        String url = parseHttpUrl(methodConfig, req);
        Boolean formDataRequest = req.get(FORM_DATA_REQUEST);
        ByteBuf content = Boolean.TRUE.equals(formDataRequest)
                        ? Unpooled.EMPTY_BUFFER
                        : Optional.ofNullable(req.get(HTTP_REQUEST_BODY_KEY))
                                    .orElseGet(() -> Optional.ofNullable(addStringContentBuffer(req))
                                                             .orElse(Unpooled.EMPTY_BUFFER));

        String methodString = req.get(AbstractHttpMappingLifeCycle.HTTP_METHOD_KEY);
        if (StringUtils.isBlank(methodString))
            methodString = methodConfig.config(AbstractHttpMappingLifeCycle.HTTP_METHOD_KEY);

        io.netty.handler.codec.http.HttpMethod method = io.netty.handler.codec.http.HttpMethod.valueOf(methodString);
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, url, content);
        httpRequest.headers().add(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        Optional.ofNullable(req.get(AbstractHttpMappingLifeCycle.COMMON_HEADER)).ifPresent(commonHeader -> commonHeader.forEach((key, value) -> httpRequest.headers().add(key,value)));
        Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).ifPresent(customerHeader -> customerHeader.forEach((key, value) -> httpRequest.headers().add(key,value)));
        Map<String, String> headerMap = Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
        headerMap.clear();

        httpRequest.headers().add("User-Agent","Remote HTTP Client on Netty, Java 8");
        httpRequest.headers().add("Host",req.get(RemoteConstant.HOST));
        httpRequest.headers().forEach(item -> headerMap.put(item.getKey(),item.getValue()));

        req.put(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE,headerMap);
        req.put(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY, httpRequest);
        return httpRequest;
    }

    public static String parseHttpUrl(RemoteMethodConfig methodConfig, RemoteReqContext req) {
        String uri = req.get(HTTP_REQUEST_URI);
        if (StringUtils.isBlank(uri)) {
            uri = methodConfig.config(HTTP_REQUEST_URI);
            if (StringUtils.isBlank(uri))
                uri = StringUtils.EMPTY;
        }

        QueryStringEncoder queryStringEncoder = new QueryStringEncoder(uri);
        Map<String, String> queries = req.get(AbstractHttpQueryLifeCycle.HTTP_QUERY_VALUE);
        Map<String, String> commonQueries = Optional.ofNullable(methodConfig.config(COMMON_QUERY)).orElseGet(HashMap::new);
        Optional.ofNullable(req.get(COMMON_QUERY)).ifPresent(commonQueries::putAll);
        if (MapUtils.isNotEmpty(commonQueries)) {
            for (Map.Entry<String, String> entry : commonQueries.entrySet()) {
                queryStringEncoder.addParam(entry.getKey(), entry.getValue());
            }
        }

        if (MapUtils.isNotEmpty(queries)) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                queryStringEncoder.addParam(entry.getKey(), entry.getValue());
            }
        }
        req.put(NettyRemoteHTTPClient.QUERY_STRING_ENCODER_GENERIC_KEY, queryStringEncoder);


        String url;
        try {
            url = queryStringEncoder.toUri().toASCIIString();
        } catch (URISyntaxException e) {
            url = queryStringEncoder.toString();
        }
        req.put(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI, url);
        return url;
    }

    public static ByteBuf addStringContentBuffer(RemoteReqContext req) {
        String charsetName = Optional.ofNullable(req.get(RemoteConstant.CHARSET)).orElse(StandardCharsets.UTF_8.name());
        String stringBody = Optional.ofNullable(req.get(STRING_BODY_KEY)).orElse(StringUtils.EMPTY);
        if (StringUtils.isBlank(stringBody))
            return null;
        Charset charset = Charset.forName(charsetName);
        return Unpooled.wrappedBuffer(stringBody.getBytes(charset));
    }

    @Override
    public void send(RemoteReqContext req, RemoteResContext res) {
        log.info("\r\n\tRemote NET Req Exec === Endpot: {}", this);
        final SimpleChannelPool simpleChannelPool = Optional.ofNullable(req.get(CHANNEL_POOL_GENERIC_KEY)).orElseGet(() -> RemoteHttpClientPoolOnNetty.simpleChannelPool(this.nodeKey));
        final Promise<FullHttpResponse> promise = Optional.ofNullable(req.get(FULL_HTTP_RESPONSE_PROMISE_KEY)).orElseGet(RemoteNettyChannelContext.NETTY_RESPONSE_PROMISE_NOTIFY_EVENT_LOOP::newPromise);

        promise.addListener((GenericFutureListener<Future<FullHttpResponse>>) future -> {
            if (!future.isSuccess()) {
                if (Objects.nonNull(future.cause())) {
                    res.setCause(future.cause());
                }

                if (future.isCancelled()) {
                    res.setCause(new IllegalStateException("请求取消"));
                }
            } else {
                FullHttpResponse fullHttpResponse = future.get();
                HttpResponseStatus status = fullHttpResponse.status();
                res.setStatus(status);
                HttpVersion httpVersion = fullHttpResponse.protocolVersion();
                res.setProtocol(httpVersion);
                Object headerObj = res.getHeaders();
                Map<String, String> headerMap;
                if (Objects.isNull(headerObj)){
                    headerMap = new HashMap<>();
                } else {
                    if (headerObj instanceof Map){
                        //noinspection unchecked
                        headerMap = (Map<String, String>) headerObj;
                    } else {
                        headerMap = new HashMap<>();
                    }
                }

                HttpHeaders headers = fullHttpResponse.headers();
                if (Objects.nonNull(headers)) {
                    for (Map.Entry<String, String> header : headers) {
                        headerMap.putIfAbsent(header.getKey(), header.getValue());
                    }
                }
                res.setHeaders(headerMap);
                ByteBuf content = fullHttpResponse.content();
                byte[] contentBuffer = ByteBufUtil.getBytes(content);
                res.setTempData(contentBuffer);
                NettyPoolUtil.releaseObject(content, fullHttpResponse);
            }
            ServerLifeCycle.countDown(req);
        });

        simpleChannelPool.acquire().addListener((GenericFutureListener<Future<Channel>>) fn -> {
            if (!fn.isSuccess() || Objects.nonNull(fn.cause())) {
                res.setCause(fn.cause());
                ServerLifeCycle.countDown(req);
                NettyPoolUtil.releaseNettyClient(simpleChannelPool, fn);
                return;
            }

            Channel channel = fn.get();
            RemoteNettyChannelContext context = RemoteNettyChannelContext.builder()
                    .resContext(res)
                    .reqContext(req)
                    .simpleChannelPool(simpleChannelPool)
                    .channel(channel)
                    .trace(this.nodeKey.getTrace())
                    .defaultPromise(promise)
                    .sslContext(this.nodeKey.getSslContext())
                    .build();

            HttpPostRequestEncoder postEncoder = req.get(POST_REQUEST_ENCODER_GENERIC_KEY);
            HttpRequest httpRequest = req.get(HTTP_REQUEST_GENERIC_KEY);
            channel.attr(RemoteNettyChannelContext.CURRENT_REQ_BOUND_WITH_THE_CHANNEL).set(context);
            writeAndFlush(res, channel, postEncoder, channel.writeAndFlush(httpRequest));

            if (Objects.nonNull(postEncoder) && postEncoder.isChunked())
                writeAndFlush(res, channel, postEncoder, channel.writeAndFlush(postEncoder));
        });
        req.put(REQUEST_SEND, Boolean.TRUE);
    }

    private void writeAndFlush(RemoteResContext res, Channel channel, HttpPostRequestEncoder postEncoder, ChannelFuture channelFuture) {
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                if (log.isDebugEnabled())
                    log.debug("Request Send Finished!");
                // 释放请求内存
                channel.flush();
            } else {
                if (future.isDone()) {
                    if (log.isDebugEnabled())
                        log.debug("Request Send Done: {}", future.isCancelled());
                }
                if (Objects.nonNull(future.cause())) {
                    log.info("Request Send Exception: {}", future.cause(), future.cause());
                    res.setThrowable(future.cause());
                }
            }
            if (Objects.nonNull(postEncoder) && postEncoder.isEndOfInput())
                postEncoder.cleanFiles();
        });
    }
}