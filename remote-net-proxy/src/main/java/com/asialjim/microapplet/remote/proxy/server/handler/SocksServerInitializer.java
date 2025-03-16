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
package com.asialjim.microapplet.remote.proxy.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                new SocksPortUnificationServerHandler(),
                SocksServerHandler.INSTANCE);
    }
}
