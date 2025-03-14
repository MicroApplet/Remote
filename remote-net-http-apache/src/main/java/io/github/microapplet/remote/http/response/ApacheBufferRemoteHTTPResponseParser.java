/*
 * Copyright 2014-2024 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
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
package io.github.microapplet.remote.http.response;

import io.github.microapplet.remote.annotation.RemoteSubProperty;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.net.mime.MimeMenu;
import io.github.microapplet.remote.net.response.parse.BufferRemoteNetResponseParser;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.util.Map;

@RemoteSubProperty("apache")
public final class ApacheBufferRemoteHTTPResponseParser extends BufferRemoteNetResponseParser {

    @Override
    protected MimeType mimeType(RemoteResContext resContext) {
        try {
            return new MimeType(contentType(resContext));
        } catch (MimeTypeParseException e) {
            return MimeMenu.UNSUPPORT_TYPE;
        }
    }

    @Override
    protected String contentType(RemoteResContext resContext) {
        //noinspection unchecked
        Map<String,String> headers = (Map<String, String>) resContext.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (!StringUtils.equalsIgnoreCase("content-type", entry.getKey()))
                continue;
            return entry.getValue();
        }
        return "*";
    }

    @Override
    protected Long contentLength(RemoteResContext resContext) {
        //noinspection unchecked
        Map<String,String> headers = (Map<String, String>) resContext.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (!StringUtils.equalsIgnoreCase("content-length", entry.getKey()))
                continue;
            String value = entry.getValue();
            return Long.parseLong(value);
        }
        return 0L;
    }

    @Override
    protected String fileName(RemoteResContext resContext) {
        //noinspection unchecked
        Map<String,String> headers = (Map<String, String>) resContext.getHeaders();
        String fileName = StringUtils.EMPTY;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (!StringUtils.equalsIgnoreCase("content-disposition", entry.getKey()))
                continue;
            String value = entry.getValue();
            if (value.contains("filename="))
                return value.substring(value.indexOf("filename=") + 9).replaceAll("\"","");
        }
        return fileName;
    }

    @Override
    protected byte[] buffer(RemoteResContext resContext) {
        return (byte[]) resContext.getTempData();
    }
}
