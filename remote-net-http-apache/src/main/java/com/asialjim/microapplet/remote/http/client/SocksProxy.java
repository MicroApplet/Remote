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

package com.asialjim.microapplet.remote.http.client;

import java.net.Proxy;
import java.net.SocketAddress;

/**
 * proxy for sun.net.SocksProxy
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2024/1/22, &nbsp;&nbsp; <em>version:1.0</em>
 */
public final class SocksProxy extends Proxy {
    private final int version;

    private SocksProxy(SocketAddress addr, int version) {
        super(Proxy.Type.SOCKS, addr);
        this.version = version;
    }

    public static SocksProxy create(SocketAddress addr, int version) {
        return new SocksProxy(addr, version);
    }

    @SuppressWarnings("unused")
    public int protocolVersion() {
        return version;
    }
}