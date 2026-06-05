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
package com.asialjim.microapplet.remote.net.jackson;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.type.MapType;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.InputStream;
import java.util.*;

@SuppressWarnings("unused")
public abstract class AbstractJacksonUtil {
    private static final Logger log = LoggerFactory.getLogger(AbstractJacksonUtil.class);

    public static final JsonMapper JSON_MAPPER;
    public static final XmlMapper XML_MAPPER;

    static {
        JSON_MAPPER = new JacksonUtil<>(JsonMapper.builder()) {
        }.objectMapper();

        XML_MAPPER = new JacksonUtil<>(XmlMapper.builder()) {
        }.objectMapper();
    }

    /**
     * 序列化为Json
     *
     * @param body {@link Object body}
     * @return {@link String }
     * @since 2024/2/26
     */
    public static String writeValueAsJsonString(Object body) {
        return writeValueAsString(body, JSON_MAPPER);
    }

    /**
     * 序列化为XML
     *
     * @param body {@link Object body}
     * @return {@link String }
     * @since 2024/2/26
     */
    public static String writeValueAsXmlString(Object body) {
        return writeValueAsString(body, XML_MAPPER);
    }

    /**
     * Json 反序列化列表
     *
     * @param json  {@link String json}
     * @param clazz {@link Class clazz}
     * @return {@link List<T> }
     * @since 2024/2/26
     */
    public static <T> List<T> json2list(String json, Class<T> clazz) {
        return toList(json, clazz, JSON_MAPPER);
    }

    /**
     * XML 反序列化为列表
     *
     * @param xml   {@link String xml}
     * @param clazz {@link Class clazz}
     * @return {@link List<T> }
     * @since 2024/2/26
     */
    public static <T> List<T> xml2list(String xml, Class<T> clazz) {
        return toList(xml, clazz, XML_MAPPER);
    }

    /**
     * Json 反序列化为Map
     *
     * @param json  {@link String json}
     * @param clazz {@link Class clazz}
     * @return {@link Map}<{@link String},{@link T}>
     * @since 2024/2/26
     */
    public static <T> Map<String, T> json2map(String json, Class<T> clazz) {
        return toMap(json, clazz, JSON_MAPPER);
    }

    /**
     * XML 反序列化为Map
     *
     * @param xml   {@link String xml}
     * @param clazz {@link Class clazz}
     * @return {@link Map}<{@link String},{@link T}>
     * @since 2024/2/26
     */
    public static <T> Map<String, T> xml2map(String xml, Class<T> clazz) {
        return toMap(xml, clazz, XML_MAPPER);
    }

    /**
     * Json 反序列化为指定类型对象
     *
     * @param json  {@link String json}
     * @param clazz {@link Class clazz}
     * @return {@link T }
     * @since 2024/2/26
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        return toObject(json, clazz, JSON_MAPPER);
    }

    /**
     * XML 反序列化为指定类型对象
     *
     * @param xml   {@link String xml}
     * @param clazz {@link Class clazz}
     * @return {@link T }
     * @since 2024/2/26
     */
    public static <T> T xml2Object(String xml, Class<T> clazz) {
        return toObject(xml, clazz, XML_MAPPER);
    }

    /**
     * 使用指定的序列化器序列化为String
     *
     * @param body   {@link Object body}
     * @param mapper {@link ObjectMapper mapper}
     * @return {@link String }
     * @since 2024/2/26
     */
    public static String writeValueAsString(Object body, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(body);
        } catch (Throwable t) {
            log.error("Write Value for: {} Exception: {}", body, t.getMessage(), t);
            if (mapper instanceof XmlMapper)
                return "</>";
            return "{}";
        }
    }

    public static String writeValueAsString(String rootName, Object body, ObjectMapper mapper) {
        try {
            return mapper.writer().withRootName(rootName).writeValueAsString(body);
        } catch (Throwable t) {
            log.error("Write Value for: {} Exception: {}", body, t.getMessage(), t);
            if (mapper instanceof XmlMapper)
                return "</>";
            return "{}";
        }
    }

    /**
     * 使用指定的反序列化器反序列化为列表
     *
     * @param stringValue {@link String stringValue}
     * @param clazz       {@link Class clazz}
     * @param mapper      {@link ObjectMapper mapper}
     * @return {@link List<T> }
     * @since 2024/2/26
     */
    public static <T> List<T> toList(String stringValue, Class<T> clazz, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return Collections.emptyList();
        if (Strings.CI.startsWith(stringValue, "\"") && Strings.CI.endsWith(stringValue, "\""))
            stringValue = mapper.readValue(stringValue, String.class);

        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
        return mapper.readValue(stringValue, javaType);
    }

    /**
     * 使用指定的反序列化器反序列化字符串为指定类型对象
     *
     * @param stringValue {@link String stringValue}
     * @param tClass      {@link Class tClass}
     * @param mapper      {@link ObjectMapper mapper}
     * @return {@link Map}<{@link String},{@link T}>
     * @since 2024/2/26
     */
    public static <T> Map<String, T> toMap(String stringValue, Class<T> tClass, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return new HashMap<>();

        if (Objects.isNull(tClass)) {
            if (Strings.CI.startsWith(stringValue, "\"") && Strings.CI.endsWith(stringValue, "\""))
                stringValue = mapper.readValue(stringValue, String.class);

            return mapper.readValue(stringValue, new TypeReference<Map<String, T>>() {
            });
        }

        MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, tClass);
        return mapper.readValue(stringValue, mapType);
    }

    /**
     * 指定反序列化器反序列化字符串为指定的类型对象
     *
     * @param stringValue {@link String stringValue}
     * @param tClass      {@link Class tClass}
     * @param mapper      {@link ObjectMapper mapper}
     * @return {@link T }
     * @since 2024/2/26
     */
    public static <T> T toObject(String stringValue, Class<T> tClass, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return null;
        if (Strings.CI.startsWith(stringValue, "\"") && Strings.CI.endsWith(stringValue, "\""))
            stringValue = mapper.readValue(stringValue, String.class);


        return mapper.readValue(stringValue, tClass);
    }

    public static JsonNode readXmlTree(String body) {
        try {
            if (Strings.CI.startsWith(body, "\"") && Strings.CI.endsWith(body, "\""))
                body = XML_MAPPER.readValue(body, String.class);

            return XML_MAPPER.readTree(body);
        } catch (Throwable t) {
            if (log.isDebugEnabled())
                log.error("String value:{} Deserializer to JsonNode Exception:{}", body, t.getMessage(), t);
            else
                //noinspection LoggingSimilarMessage
                log.error("String value:{} Deserializer to JsonNode Exception:{}", body, t.getMessage());
            return NullNode.instance;
        }
    }

    public static JsonNode readXmlTree(InputStream body) {
        try {
            return XML_MAPPER.readTree(body);
        } catch (Throwable t) {
            if (log.isDebugEnabled())
                log.error("String value:{} Deserializer to JsonNode Exception:{}", body, t.getMessage(), t);
            else
                //noinspection LoggingSimilarMessage
                log.error("String value:{} Deserializer to JsonNode Exception:{}", body, t.getMessage());
            return NullNode.instance;
        }
    }
}