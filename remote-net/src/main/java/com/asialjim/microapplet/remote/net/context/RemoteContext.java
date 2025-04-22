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
package com.asialjim.microapplet.remote.net.context;

import com.asialjim.microapplet.remote.context.GenericKey;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.thread.NamedThreadFactory;
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
public class RemoteContext {
    public static final GenericKey<Boolean>                     REQUEST_SEND                                = GenericKey.keyOf("SEND_NET_REQUEST");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_GROUP                     = new NamedThreadFactory("RELG");
    public static final ThreadFactory                           REMOTE_EVENT_LOOP_RESULT                    = new NamedThreadFactory("RELR");

    private RemoteReqContext                reqContext;
    private RemoteResContext                resContext;
    private SSLContext                      sslContext;
    private String                          trace;
}