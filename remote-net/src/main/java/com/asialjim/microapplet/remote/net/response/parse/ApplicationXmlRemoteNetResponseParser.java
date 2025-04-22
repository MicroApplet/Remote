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

import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.net.jackson.AbstractJacksonUtil;
import com.asialjim.microapplet.remote.net.mime.MimeMenu;
import com.asialjim.microapplet.remote.net.response.BaseRemoteNetResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import java.util.ArrayList;
import java.util.List;

public class ApplicationXmlRemoteNetResponseParser extends BaseRemoteNetResponseParser {
    public static final ApplicationXmlRemoteNetResponseParser INSTANCE = new ApplicationXmlRemoteNetResponseParser();
    private static final Logger log = LoggerFactory.getLogger(ApplicationXmlRemoteNetResponseParser.class);
    private static final List<MimeType> MIME_TYPES = new ArrayList<>();
    static {
        MIME_TYPES.add(MimeMenu.APPLICATION_XML);
        MIME_TYPES.add(MimeMenu.TEXT_XML);
    }

    @Override
    protected void doParse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        String xml = (String) resContext.getTempData();
        Class<?> returnClass = methodConfig.getReturnClass();

        Object res = AbstractJacksonUtil.xml2Object(xml, returnClass);
        log.info("\r\n\tRemote NET Res Data <<< Client:{} <<< {}", methodConfig.getRemoteName(),res);
        resContext.setData(res);
    }

    @Override
    public List<MimeType> support() {
        return MIME_TYPES;
    }

    @Override
    public int order() {
        return 0;
    }
}
