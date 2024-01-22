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
package io.github.microapplet.remote.http.pool;

import io.github.microapplet.remote.net.context.RemoteNetNodeKey;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * HTTP 连接池处理器
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/13, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class HttpChannelPoolHandlerOnNetty implements ChannelPoolHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpChannelPoolHandlerOnNetty.class);
    private final RemoteNetNodeKey nodeKey;

    public HttpChannelPoolHandlerOnNetty(RemoteNetNodeKey nodeKey) {
        this.nodeKey = nodeKey;
    }

    @Override
    public void channelReleased(Channel channel) {
        log.info("向连接池归还连接, ChannelId: {}", channel.id());
    }

    @Override
    public void channelAcquired(Channel channel) {
        log.info("从连接池获取连接, ChannelId: {}", channel.id());
    }

    @Override
    public void channelCreated(Channel channel) throws SSLException {
        log.info("连接池创建连接, ChannelId: {}", channel.id());
        SocketChannel ch = (SocketChannel) channel;
        ch.config().setKeepAlive(true);
        ch.config().setTcpNoDelay(true);

        ChannelPipeline pipeline = ch.pipeline();
        if (nodeKey.proxyEnable())
            pipeline.addFirst(new Socks5ProxyHandler(new InetSocketAddress(nodeKey.getProxyHost(), nodeKey.getProxyPort())));

        SslHandler sslHandler;
        if (Objects.nonNull(this.nodeKey.getSslContext())) {
            SSLEngine sslEngine = nodeKey.getSslContext().createSSLEngine();
            sslEngine.setUseClientMode(true);
            sslHandler = new SslHandler(sslEngine);
        } else if (StringUtils.equalsIgnoreCase("https", nodeKey.getSchema())) {
            SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            sslHandler = sslContext.newHandler(ch.alloc());
        } else {
            sslHandler = null;
        }

        if (Objects.nonNull(sslHandler))
            pipeline.addLast("ssl", sslHandler);

        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("decompressor", new HttpContentDecompressor());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1 << 25));

        try {
            LogLevel level = LogLevel.valueOf(nodeKey.getLogLevel().toUpperCase());
            pipeline.addLast("log", new LoggingHandler(level));
        } catch (Throwable ignored) {

        }

        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("handler", new RemoteHttpResponseHandler());
    }
}