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
 package com.asialjim.microapplet.remote.http.pool;

import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;

 @AllArgsConstructor
 public final class RemoteHttpClientPoolMap extends AbstractChannelPoolMap<RemoteNetNodeKey, SimpleChannelPool> {
     private final Bootstrap strap;
     private final int maxConnectionPerRout;
     private final int acquireTimeoutMillis;

     @Override
     protected SimpleChannelPool newPool(RemoteNetNodeKey key) {
         return new FixedChannelPool(strap.remoteAddress(new InetSocketAddress(key.getHost(), key.getPort())),
                 new HttpChannelPoolHandlerOnNetty(key),
                 ChannelHealthChecker.ACTIVE,
                 FixedChannelPool.AcquireTimeoutAction.FAIL,
                 acquireTimeoutMillis,
                 maxConnectionPerRout,
                 Integer.MAX_VALUE,
                 true);
     }
 }
