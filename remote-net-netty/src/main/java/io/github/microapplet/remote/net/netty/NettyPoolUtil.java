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
package io.github.microapplet.remote.net.netty;

import io.github.microapplet.remote.net.netty.context.RemoteNettyChannelContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.github.microapplet.remote.net.netty.context.RemoteNettyChannelContext.CURRENT_REQ_BOUND_WITH_THE_CHANNEL;


/**
 * Netty 工具
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/14, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class NettyPoolUtil {
    private static final Logger log = LoggerFactory.getLogger(NettyPoolUtil.class);


    @SuppressWarnings("unused")
    public static void get(Promise<?> future, CountDownLatch countDownLatch, int timeout) {
        if (!future.isDone()) {

            if (!future.isDone()) {
                try {
                    boolean await = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
                    if (!await) {
                        future.setFailure(new TimeoutException("等待超时，最大允许超时时间：" + timeout + "毫秒"));
                    }
                } catch (Throwable t) {
                    future.setFailure(new TimeoutException("等待超时，最大允许超时时间：" + timeout + "毫秒"));
                }
            }

        }
        if (future.isSuccess())
            future.getNow();
    }

    public static void releaseNettyClient(SimpleChannelPool simpleChannelPool, Future<Channel> future) {
        Channel channel = future.getNow();
        if (Objects.isNull(channel))
            return;
        ChannelFuture channelFuture = channel.closeFuture();
        channelFuture.addListener(future1 -> simpleChannelPool.release(channel));
    }


    public static void releaseChannel(ChannelHandlerContext ctx) {
        if (Objects.isNull(ctx) || Objects.isNull(ctx.channel()))
            return;

        try {
            RemoteNettyChannelContext nettyHttpRequestContext = ctx.channel().attr(CURRENT_REQ_BOUND_WITH_THE_CHANNEL).get();
            if (Objects.isNull(nettyHttpRequestContext))
                return;
            SimpleChannelPool simpleChannelPool = nettyHttpRequestContext.getSimpleChannelPool();
            if (Objects.nonNull(simpleChannelPool)) {
                simpleChannelPool.release(ctx.channel());
            } else ctx.channel().close();
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Release Netty Channel Exception Happen: {}", t.getMessage(), t);
            }

            try {
                ctx.channel().close();
            } catch (Throwable ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Close Netty Channel Exception Happen: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public static void releaseObject(Object... args) {
        if (ArrayUtils.isEmpty(args))
            return;

        for (Object arg : args) {
            if (arg instanceof Collection) {
                //noinspection unchecked
                releaseObject((Collection<Object>) arg);
            } else doRelease(args);
        }
    }

    public static void releaseObject(Collection<Object> args) {
        if (CollectionUtils.isEmpty(args))
            return;

        for (Object arg : args) {
            if (arg instanceof Collection) {
                //noinspection unchecked
                releaseObject((Collection<Object>) arg);
            } else doRelease(arg);
        }
    }

    private static void doRelease(Object arg) {
        try {
            if (Objects.isNull(arg))
                return;

            if (arg instanceof ReferenceCounted counted) {
                int i = counted.refCnt();
                if (log.isDebugEnabled())
                    log.debug("Release Reference: {} Counted: {}", counted.getClass().getName(), i);
                if (i <= 0)
                    return;

                if (counted instanceof EmptyByteBuf) {
                    ReferenceCountUtil.safeRelease(counted, i);
                    if (log.isDebugEnabled())
                        log.debug("Release EmptyByteBuf Reference: {} Counted: {}, Number: 0 Result:{}", counted.getClass().getName(), i, true);
                    return;
                }

                if (counted instanceof ByteBufHolder holder) {
                    ByteBuf content = holder.content();
                    if (content instanceof EmptyByteBuf) {
                        ReferenceCountUtil.safeRelease(counted, i);
                        if (log.isDebugEnabled())
                            log.debug("Release EmptyByteBufHolder Reference: {} Counted: {}, Number: 0 Result:{}", counted.getClass().getName(), i, true);
                        return;
                    }
                }

                try {
                    boolean release = ReferenceCountUtil.release(counted, i);
                    if (log.isDebugEnabled())
                        log.info("Release Reference: {} Counted: {} Number: {} Result: {}", counted.getClass().getName(), i, counted.refCnt(), release);
                } catch (Throwable t) {
                    if (log.isDebugEnabled())
                        log.info("Release Reference: {} Counted: {} Exception Happen: {}", counted.getClass().getName(), i, t.getMessage(), t);
                }
            }
        } catch (Throwable t) {
            // do nothing here
        }
    }

}