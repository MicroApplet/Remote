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
package com.asialjim.microapplet.remote.net.netty.context;

import com.asialjim.microapplet.remote.context.GenericKey;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.thread.NamedThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import java.util.concurrent.ThreadFactory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoteNettyChannelContext {
    public static final AttributeKey<RemoteNettyChannelContext> CURRENT_REQ_BOUND_WITH_THE_CHANNEL          = AttributeKey.valueOf("CURRENT_REQ_BOUND_WITH_THE_CHANNEL");
    public static final GenericKey<Promise<?>>                  DEFAULT_PROMISE_RES_CONTEXT_KEY             = GenericKey.keyOf("DEFAULT_PROMISE_RES_CONTEXT_KEY");
    public static final GenericKey<Boolean>                     REQUEST_SEND                                = GenericKey.keyOf("SEND_NET_REQUEST");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_GROUP                     = new NamedThreadFactory("RELG");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_RESULT                    = new NamedThreadFactory("RELR");
    public static final EventLoopGroup                          group                                       = new NioEventLoopGroup(0, REMOTE_EVENT_LOOP_GROUP);
    public static final DefaultEventLoop                        NETTY_RESPONSE_PROMISE_NOTIFY_EVENT_LOOP    = new DefaultEventLoop(group, REMOTE_EVENT_LOOP_RESULT);

    private Channel                         channel;
    private RemoteReqContext                reqContext;
    private RemoteResContext                resContext;
    private SimpleChannelPool               simpleChannelPool;
    private Promise<?>                      defaultPromise;
    private SSLContext                      sslContext;
    private String                          trace;
}