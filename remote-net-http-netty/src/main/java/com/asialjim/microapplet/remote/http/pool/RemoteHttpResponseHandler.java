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
package com.asialjim.microapplet.remote.http.pool;

import com.asialjim.microapplet.remote.net.netty.context.RemoteNettyChannelContext;
import com.asialjim.microapplet.remote.net.netty.response.BaseChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static com.asialjim.microapplet.remote.net.netty.NettyPoolUtil.releaseChannel;

@ChannelHandler.Sharable
public final class RemoteHttpResponseHandler extends BaseChannelInboundHandlerAdapter<FullHttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(RemoteHttpResponseHandler.class);

    public RemoteHttpResponseHandler() {
        super(FullHttpResponse.class);
    }

    @Override
    protected void doChannelRead(ChannelHandlerContext ctx, FullHttpResponse req) {
        channelRead0(ctx,req);
    }

    //@Override
    private void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
        log.info("收到服务器:{} 响应：{}",ctx.channel().id(), response.status());
        RemoteNettyChannelContext channelContext = ctx.channel().attr(RemoteNettyChannelContext.CURRENT_REQ_BOUND_WITH_THE_CHANNEL).get();

        //noinspection unchecked
        Promise<FullHttpResponse> defaultPromise = (Promise<FullHttpResponse>) channelContext.getDefaultPromise();
        defaultPromise.setSuccess(response);

        releaseChannel(ctx);
        MDC.clear();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("与服务器端连接建立， ChannelId: {}", ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("与服务端通讯异常， ChannelId: {}, Exception:{}", ctx.channel().id(), cause.getMessage(), cause);
        releaseChannel(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与服务端断开连接，ChannelId：{}", ctx.channel().id());
        releaseChannel(ctx);
    }
}