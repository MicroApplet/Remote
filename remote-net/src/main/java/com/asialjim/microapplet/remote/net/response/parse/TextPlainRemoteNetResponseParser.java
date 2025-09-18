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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TextPlainRemoteNetResponseParser extends BaseRemoteNetResponseParser {
    public static final GenericKey<String> TEXT_RES = GenericKey.keyOf("text/*_net_response");
    private static final List<MimeType> MIME_TYPES = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(TextPlainRemoteNetResponseParser.class);

    static {
        MIME_TYPES.add(MimeMenu.ANY_TEXT);
        MIME_TYPES.add(MimeMenu.APPLICATION_JSON);
        MIME_TYPES.add(MimeMenu.APPLICATION_XML);
        MIME_TYPES.add(MimeMenu.APPLICATION_JAVASCRIPT);
    }

    @SuppressWarnings("unused")
    public static boolean resultSupport(RemoteResContext resContext) {
        return resultContextSupport(resContext, MIME_TYPES);
    }

    @Override
    public final int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected final void doParse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        if (StringUtils.isNotBlank(resContext.property(TEXT_RES)))
            return;
        Charset charset = charset(resContext);
        byte[] bytes = responseContent(resContext);
        String responseString = new String(bytes, charset);
        log.info("Remote NET Res Body <<< Client:{} <<< {}", methodConfig.getRemoteName(), responseString);
        resContext.property(TEXT_RES, responseString);
        resContext.setTempData(responseString);
    }

    /**
     * 获取字符集
     *
     * @param resContext {@link RemoteResContext resContext}
     * @return {@link Charset 字符集}
     * @since 2023/9/27
     */
    protected abstract Charset charset(RemoteResContext resContext);

    protected byte[] responseContent(RemoteResContext resContext) {
        Object tempData = resContext.getTempData();
        if (Objects.isNull(tempData))
            return new byte[0];

        if (tempData instanceof byte[])
            return (byte[]) tempData;

        if (tempData instanceof InputStream inputStream) {
            try {
                return IOUtils.toByteArray(inputStream);
            } catch (Throwable t) {
                return new byte[0];
            }
        }

        return new byte[0];
    }

    @Override
    public final List<MimeType> support() {
        return MIME_TYPES;
    }

    @Override
    protected boolean support(MimeType source, MimeType target) {
        String sourcePrimaryType = source.getPrimaryType();
        String sourceSubType = source.getSubType();

        String targetPrimaryType = target.getPrimaryType();
        String targetSubType = target.getSubType();
        if (StringUtils.equals(targetPrimaryType, "*"))
            return true;

        if (!StringUtils.equals(targetPrimaryType, sourcePrimaryType))
            return false;

        if (StringUtils.contains(sourceSubType, "event-stream"))
            return false;

        if (StringUtils.equals(targetSubType,"*"))
            return true;

        return true;
    }
}