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

package com.asialjim.microapplet.remote.net.response.parse;

import com.asialjim.microapplet.remote.context.GenericKey;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.net.mime.MimeMenu;
import com.asialjim.microapplet.remote.net.response.BaseRemoteNetResponseParser;
import com.asialjim.microapplet.remote.net.response.RemoteNetResponseParser;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * TEXT 服务器事件推送响应结果转换器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/5/8, &nbsp;&nbsp; <em>version:1.0</em>
 */
public class TextEventStreamRemoteNetResponseParser extends BaseRemoteNetResponseParser {
    public static final TextEventStreamRemoteNetResponseParser INSTANCE = new TextEventStreamRemoteNetResponseParser();
    public static final GenericKey<BufferedReader> TEXT_EVENT_STREAM = GenericKey.keyOf("text/event-stream");

    private static final List<MimeType> MIME_TYPES = new ArrayList<>();

    static {
        MimeType textEventStream = MimeMenu.createConstant("text", "event-stream");
        MIME_TYPES.add(textEventStream);
    }

    @Override
    public final int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected final void doParse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        if (Objects.nonNull(resContext.property(TEXT_EVENT_STREAM)))
            return;

        Object tempData = resContext.getTempData();

        InputStream is = null;
        if (Objects.isNull(tempData))
            is = new ByteArrayInputStream(new byte[0]);

        if (tempData instanceof byte[])
            is = new ByteArrayInputStream((byte[]) tempData);

        if (tempData instanceof InputStream)
            is = (InputStream) tempData;

        if (Objects.isNull(is))
            is = new ByteArrayInputStream(new byte[0]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        resContext.property(TEXT_EVENT_STREAM, reader);
        resContext.setTempData(is);
        resContext.property(RemoteNetResponseParser.parsed,true);
    }

    /**
     * 获取字符集
     *
     * @param resContext {@link RemoteResContext resContext}
     * @return {@link Charset 字符集}
     * @since 2023/9/27
     */
    public Charset charset(RemoteResContext resContext) {
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
            if (StringUtils.equalsAnyIgnoreCase(name, "charset", "encoding")) {
                charsetName = parameters.get(name);
                break;
            }
        }

        return StringUtils.isBlank(charsetName) ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
    }

    @Override
    public final List<MimeType> support() {
        return MIME_TYPES;
    }
}