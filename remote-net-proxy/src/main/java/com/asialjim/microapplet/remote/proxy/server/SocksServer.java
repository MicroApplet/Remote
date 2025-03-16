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
package com.asialjim.microapplet.remote.proxy.server;

import com.asialjim.microapplet.remote.proxy.server.handler.SocksServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public final class SocksServer implements ProxyServer {
    private int boss;
    private int worker;
    private int port;
    private String logLevel;

    public static void main(String[] args) {
        ProxyServer server = new SocksServer(1, 0, 13001, LogLevel.ERROR.name());
        server.start();
    }

    private static void start(int boss, int worker, int port, String logLevel) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(boss);
        EventLoopGroup workerGroup = new NioEventLoopGroup(worker);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.valueOf(logLevel)))
                    .childHandler(new SocksServerInitializer());
            try {
                b.bind(port).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void start() {
        start(boss, worker, port, logLevel);
    }

    public void setLogLevel(String logLevel) {
        if (StringUtils.isBlank(logLevel))
            logLevel = LogLevel.INFO.name();
        logLevel = StringUtils.upperCase(logLevel);
        try {
            LogLevel.valueOf(logLevel);
        } catch (Throwable t) {
            logLevel = LogLevel.INFO.name();
        }
        this.logLevel = logLevel;
    }

}