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
import com.asialjim.microapplet.remote.net.mime.MimeMenu;
import com.asialjim.microapplet.remote.net.response.BaseRemoteNetResponseParser;
import com.asialjim.microapplet.remote.net.response.BufferResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BufferRemoteNetResponseParser extends BaseRemoteNetResponseParser {
    private static final Logger log = LoggerFactory.getLogger(BufferRemoteNetResponseParser.class);
    private static final List<MimeType> SUPPORT = Stream.of(
            MimeMenu.ANY_IMAGE, MimeMenu.ANY_AUDIO, MimeMenu.ANY_VIDEO,
            MimeMenu.APPLICATION_BINARY, MimeMenu.OCTET_STREAM, MimeMenu.PDF,
            MimeMenu.OOXML_SHEET, MimeMenu.OOXML_DOCUMENT, MimeMenu.OOXML_PRESENTATION,
            MimeMenu.MICROSOFT_OUTLOOK, MimeMenu.MICROSOFT_EXCEL, MimeMenu.MICROSOFT_POWERPOINT, MimeMenu.MICROSOFT_WORD
    ).collect(Collectors.toList());
    
    @SuppressWarnings("unused")
    public static boolean resultSupport(RemoteResContext resContext){
        return resultContextSupport(resContext,SUPPORT);
    }




    @Override
    protected final void doParse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        Class<?> returnClass = methodConfig.getReturnClass();
        if (BufferResponse.class.isAssignableFrom(returnClass)) {
            resContext.setCause(new IllegalArgumentException("Remote Client Return Type: " + methodConfig.getRemoteName() + " must implement " + BufferResponse.class.getSimpleName()));
            return;
        }

        Optional<Constructor<?>> opt = Arrays.stream(returnClass.getConstructors()).filter(item -> item.getParameters().length == 0).findFirst();
        if (!opt.isPresent()){
            resContext.setCause(new IllegalArgumentException("Remote Client Return Type: " + methodConfig.getRemoteName() + " must has Default Constructor"));
            return;
        }

        try {
            Constructor<?> constructor = opt.get();
            BufferResponse o = (BufferResponse) constructor.newInstance();
            o.setBuffer(buffer(resContext));
            o.setFileName(fileName(resContext));
            o.setContentLength(contentLength(resContext));
            o.setContentType(contentType(resContext));
            o.setMimeType(mimeType(resContext));

            int size = Optional.ofNullable(o.getBuffer()).map(item -> item.length).orElse(0);
            log.info("\r\n\tRemote NET Res Body <<< Client:{} <<< @Buffer Size: {}B, {}KB, {}MB, {}GB", methodConfig.getRemoteName(), size, size >> 10, size >> 10 >> 10, size >> 10 >> 10 >> 10);
            log.info("\r\n\tRemote NET Res Data <<< Client:{} <<< {}", methodConfig.getRemoteName(), o);
            resContext.setData(o);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件的媒体类型
	 * @param resContext {@link RemoteResContext resContext}
     * @return {@link MimeType }
     * @since 2023/9/28
     */
    protected abstract MimeType mimeType(RemoteResContext resContext);

    /**
     * 获取文件的媒体类型
     *
	 * @param resContext {@link RemoteResContext resContext}
     * @return {@link String }
     * @since 2023/9/28
     */
    protected abstract String contentType(RemoteResContext resContext);

    /**
     * 文件长度
	 * @param resContext {@link RemoteResContext resContext}
     * @return {@link Long }
     * @since 2023/9/28
     */
    protected abstract Long contentLength(RemoteResContext resContext);

    /**
     * 获取文件名
	 * @param resContext {@link RemoteResContext resContext}
     * @return {@link String }
     * @since 2023/9/28
     */
    protected abstract String fileName(RemoteResContext resContext);

    /**
     * 获取文件内容
	 * @param resContext {@link RemoteResContext resContext}
     * @return {@link Byte}[]
     * @since 2023/9/28
     */
    protected abstract byte[] buffer(RemoteResContext resContext);

    @Override
    public List<MimeType> support() {
        return SUPPORT;
    }

    @Override
    public final int order() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    protected final boolean support(MimeType source, MimeType target) {
        return support().stream().anyMatch(item -> item.match(target));
    }
}