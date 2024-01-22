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
package io.github.microapplet.remote.net.client;

import io.github.microapplet.remote.client.RemoteClient;
import io.github.microapplet.remote.context.GenericKey;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remote 网络客户端
 *
 * @author Copyright  © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/9/26, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public interface RemoteNetClient extends RemoteClient {
    GenericKey<RemoteNetClient> REMOTE_NET_CLIENT_GENERIC_KEY = GenericKey.keyOf("remote_net_client_key");
    Map<RemoteNetNodeKey, RemoteNetClient> REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP = new ConcurrentHashMap<>();
}