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

package io.github.microapplet.remote.net.response;

import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.net.mime.MimeMenu;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimeType;
import java.util.*;

/**
 * HTTP 相应结果解析器
 *
 * @author Copyright  © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/9/27, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public abstract class BaseRemoteNetResponseParser implements RemoteNetResponseParser {

    protected static boolean resultContextSupport(RemoteResContext resContext, List<MimeType> types) {
        Object headers = resContext.getHeaders();
        if (!(headers instanceof Map))
            return false;

        //noinspection unchecked
        Map<String,String> headerMap = (Map<String, String>) headers;
        String contentType = headerMap.entrySet().stream().filter(item -> StringUtils.equalsIgnoreCase("content-type", item.getKey())).findFirst().map(Map.Entry::getValue).orElse(MimeMenu.UNSUPPORT);
        MimeType mimeType = MimeMenu.createConstant(contentType);
        return types.stream().anyMatch(item -> item.match(mimeType));
    }

    protected abstract void doParse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext);

    protected boolean support(MimeType source, MimeType target) {
        return source.match(target);
    }

    @Override
    public final void parse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        Class<?> returnClass = methodConfig.getReturnClass();
        Object data = resContext.getData();
        if (Objects.nonNull(data) && returnClass.isAssignableFrom(data.getClass()))
            return;

        if (Optional.ofNullable(support()).orElse(Collections.emptyList()).stream().noneMatch(item -> support(item,mediaType)))
            return;

        doParse(mediaType,methodConfig,resContext);
    }
}