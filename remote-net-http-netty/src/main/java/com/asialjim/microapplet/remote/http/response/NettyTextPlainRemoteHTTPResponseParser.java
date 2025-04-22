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
package com.asialjim.microapplet.remote.http.response;

import com.asialjim.microapplet.remote.annotation.Primary;
import com.asialjim.microapplet.remote.annotation.RemoteSubProperty;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.net.mime.MimeMenu;
import com.asialjim.microapplet.remote.net.response.parse.TextPlainRemoteNetResponseParser;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

@Primary
@RemoteSubProperty("netty")
public final class NettyTextPlainRemoteHTTPResponseParser extends TextPlainRemoteNetResponseParser {
    @Override
    protected Charset charset(RemoteResContext resContext) {
        Object headers = resContext.getHeaders();
        if (Objects.isNull(headers))
            return StandardCharsets.UTF_8;

        if (!(headers instanceof Map))
            return StandardCharsets.UTF_8;

        //noinspection unchecked
        Map<String, String> headerMap = (Map<String, String>) headers;
        String contentType = headerMap.entrySet().stream().filter(item -> StringUtils.equalsIgnoreCase(item.getKey(), "content-type")).map(Map.Entry::getValue).findFirst().orElse(StringUtils.EMPTY);
        MimeType mimeType = MimeMenu.createConstant(contentType);
        MimeTypeParameterList parameters = mimeType.getParameters();
        if (Objects.isNull(parameters))
            return StandardCharsets.UTF_8;

        String charsetName = StringUtils.EMPTY;
        //noinspection unchecked
        Enumeration<String> names = parameters.getNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (StringUtils.equalsAnyIgnoreCase(name, "charset","encoding")) {
                charsetName = parameters.get(name);
                break;
            }
        }

        return StringUtils.isBlank(charsetName) ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
    }

    @Override
    protected byte[] responseContent(RemoteResContext resContext) {
        return (byte[]) resContext.getTempData();
    }
}
