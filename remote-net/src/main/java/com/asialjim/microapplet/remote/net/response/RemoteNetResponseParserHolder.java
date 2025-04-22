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
package com.asialjim.microapplet.remote.net.response;

import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.loader.RemoteClassLoader;
import com.asialjim.microapplet.remote.net.response.parse.ApplicationJsonRemoteNetResponseParser;
import com.asialjim.microapplet.remote.net.response.parse.ApplicationXmlRemoteNetResponseParser;
import com.asialjim.microapplet.remote.net.response.parse.BufferRemoteNetResponseParser;
import com.asialjim.microapplet.remote.net.response.parse.TextPlainRemoteNetResponseParser;

import javax.activation.MimeType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class RemoteNetResponseParserHolder {
    private static final List<RemoteNetResponseParser> RESPONSE_PARSERS = new ArrayList<>();
    private static final Set<RemoteNetResponseParser> RESPONSE_SET = new LinkedHashSet<>();

    static {
        addParser(TextPlainRemoteNetResponseParser.class);
        addParser(BufferRemoteNetResponseParser.class);

        addResponseParser(ApplicationJsonRemoteNetResponseParser.INSTANCE);
        addResponseParser(ApplicationXmlRemoteNetResponseParser.INSTANCE);
    }

    static void addParser(Class<? extends RemoteNetResponseParser> clazz) {
        for (Class<?> aClass : RemoteClassLoader.subClasses(clazz)) {
            //noinspection unchecked
            Constructor<? extends RemoteNetResponseParser>[] declaredConstructors = (Constructor<? extends RemoteNetResponseParser>[]) aClass.getDeclaredConstructors();
            Optional<Constructor<? extends RemoteNetResponseParser>> constructorOptional = Arrays.stream(declaredConstructors).filter(item -> item.getParameterCount() == 0).filter(item -> item.getModifiers() == Modifier.PUBLIC).findFirst();

            if (!constructorOptional.isPresent())
                throw new IllegalStateException(aClass.getName() + "未提供默认构造函数");

            Constructor<? extends RemoteNetResponseParser> constructor = constructorOptional.get();

            try {
                addResponseParser(constructor.newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void parse(MimeType mediaType, RemoteMethodConfig methodConfig, RemoteResContext resContext) {
        for (RemoteNetResponseParser parser : RESPONSE_PARSERS) {
            if (parser.support(resContext))
                parser.parse(mediaType, methodConfig, resContext);
        }
    }

    public static void addResponseParser(RemoteNetResponseParser parser) {
        if (RESPONSE_SET.contains(parser))
            return;
        RESPONSE_PARSERS.add(parser);
        RESPONSE_SET.add(parser);
        RESPONSE_PARSERS.sort(Comparator.comparingInt(RemoteNetResponseParser::order));
    }
}