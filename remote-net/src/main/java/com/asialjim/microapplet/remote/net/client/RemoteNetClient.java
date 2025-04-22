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
package com.asialjim.microapplet.remote.net.client;

import com.asialjim.microapplet.remote.client.RemoteClient;
import com.asialjim.microapplet.remote.context.GenericKey;
import com.asialjim.microapplet.remote.net.context.RemoteNetNodeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RemoteNetClient extends RemoteClient {
    GenericKey<RemoteNetClient> REMOTE_NET_CLIENT_GENERIC_KEY = GenericKey.keyOf("remote_net_client_key");
    Map<RemoteNetNodeKey, RemoteNetClient> REMOTE_NET_NODE_KEY_REMOTE_NET_CLIENT_MAP = new ConcurrentHashMap<>();
}