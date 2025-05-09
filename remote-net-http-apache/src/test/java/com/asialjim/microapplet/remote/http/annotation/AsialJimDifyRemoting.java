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

package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.http.annotation.body.JsonBody;
import com.asialjim.microapplet.remote.http.annotation.meta.Message;
import com.asialjim.microapplet.remote.http.annotation.meta.MessageRes;
import com.asialjim.microapplet.remote.lifecycle.callback.After;
import com.asialjim.microapplet.remote.net.annotation.Server;

/**
 * AI智能体客户端
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/5/7, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Server(schema = "https", host = "ai.api.asialjim.cn", port = 443,timeout = 200000)
public interface AsialJimDifyRemoting {


    @HttpMapping(
            method = HttpMethod.POST,
            uri = "/v1/chat-messages",
            headers = {
                    @HttpHeader(name = "Authorization",value = "Bearer app-th8e2FVIdJZSOKZYmC49ecQv")
            })
    void chat(@JsonBody Message body, After after);
}