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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoteNetNodeKey implements Serializable {
    private static final long serialVersionUID = -2145609626837779738L;

    private String                          schema;
    private String                          host;
    private int                             port;
    private String                          proxyHost;
    private Integer                         proxyPort;
    private SSLContext                      sslContext;
    private int                             timeout;
    private String                          logLevel;
    private String                          trace;

    public int getTimeout() {
        return timeout == 0 ? 5000 : timeout;
    }

    public boolean proxyEnable(){
        return StringUtils.isNotBlank(proxyHost) && Objects.nonNull(proxyPort) && proxyPort > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteNetNodeKey nodeKey = (RemoteNetNodeKey) o;
        return nodeKey.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        Integer sslHashCode = Optional.ofNullable(this.sslContext).map(Object::hashCode).orElse(0);
        String proxyHost = StringUtils.isNotBlank(getProxyHost()) ? getProxyHost() : StringUtils.EMPTY;
        String proxyPort = Objects.isNull(getProxyPort()) ? StringUtils.EMPTY : String.valueOf(getProxyPort());
        return String.format("%s:%d:SSL:%s:PROXY:%s:%s", this.host, this.port, sslHashCode, proxyHost,proxyPort).hashCode();
    }
}