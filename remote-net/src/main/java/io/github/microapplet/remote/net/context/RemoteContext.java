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
package io.github.microapplet.remote.net.context;

import io.github.microapplet.remote.context.GenericKey;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.thread.NamedThreadFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import java.util.concurrent.ThreadFactory;

/**
 * Remote 网络管道上下文
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/14, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoteContext {
    public static final GenericKey<Boolean>                     REQUEST_SEND                                = GenericKey.keyOf("SEND_NET_REQUEST");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_GROUP                     = new NamedThreadFactory("RELG");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_RESULT                    = new NamedThreadFactory("RELR");

    private RemoteReqContext                reqContext;
    private RemoteResContext                resContext;
    private SSLContext                      sslContext;
    private String                          trace;
}