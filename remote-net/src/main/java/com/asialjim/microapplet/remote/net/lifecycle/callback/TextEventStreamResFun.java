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

package com.asialjim.microapplet.remote.net.lifecycle.callback;

import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.lifecycle.callback.After;
import com.asialjim.microapplet.remote.net.response.parse.TextEventStreamRemoteNetResponseParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.asialjim.microapplet.remote.net.response.parse.TextEventStreamRemoteNetResponseParser.TEXT_EVENT_STREAM;

/**
 * 文本服务器事件响应结果处理器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/5/8, &nbsp;&nbsp; <em>version:1.0</em>
 */
public interface TextEventStreamResFun extends After {

    @Override
    default void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        Charset charset = TextEventStreamRemoteNetResponseParser.INSTANCE.charset(res);
        BufferedReader reader = res.property(TEXT_EVENT_STREAM);
        process(reader,charset);
    }

    void process(BufferedReader reader, Charset charset);
}