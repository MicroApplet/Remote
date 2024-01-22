/*
 * Copyright 2014-2023 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
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
 package io.github.microapplet.remote.net.netty.response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;

import static io.github.microapplet.remote.net.netty.NettyPoolUtil.releaseObject;

 /**
  * Remote 基础处理器
  *
  * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
  * @version 3.0
  * @since 2023/7/19, &nbsp;&nbsp; <em>version:3.0</em>, &nbsp;&nbsp; <em>java version:8</em>
  */
 @AllArgsConstructor
 public abstract class BaseChannelInboundHandlerAdapter<T> extends ChannelInboundHandlerAdapter {
     private final Class<T> targetClass;

     @Override
     public void channelRead(ChannelHandlerContext ctx, Object msg)  {
         if (targetClass.isAssignableFrom(msg.getClass())){
             //noinspection unchecked
             doChannelRead(ctx, (T) msg);
             return;
         }
         releaseObject(msg);
     }

     protected abstract void doChannelRead(ChannelHandlerContext ctx, T req);
 }