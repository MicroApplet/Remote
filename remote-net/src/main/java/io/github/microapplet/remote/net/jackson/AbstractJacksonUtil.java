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
package io.github.microapplet.remote.net.jackson;

import com.ctc.wstx.api.WstxOutputProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于 Jackson 的基础工具
 *
 * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0
 * @since 2023/7/17, &nbsp;&nbsp; <em>version:3.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public abstract class AbstractJacksonUtil {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    public static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final Logger log = LoggerFactory.getLogger(AbstractJacksonUtil.class);

    static {
        // JSON
        // 转成对象时，可以忽略多余参数
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 大小写不敏感
        //noinspection deprecation
        JSON_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        JSON_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // XML
        XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        XML_MAPPER.getFactory().getXMLOutputFactory().setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, true);
        // 转成对象时，可以忽略多余参数
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 大小写不敏感
        //noinspection deprecation
        XML_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        XML_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }


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

    @SuppressWarnings("unused")
    public static String writeValueAsJsonString(Object body) {
        try {
            return JSON_MAPPER.writeValueAsString(body);
        } catch (Throwable t) {
            log.error("Write Value for: {} Exception: {}", body, t.getMessage(), t);
            return "{}";
        }
    }

    @SuppressWarnings("unused")
    public static String writeValueAsXmlString(Object body) {
        try {
            return XML_MAPPER.writeValueAsString(body);
        } catch (Throwable t) {
            log.error("Write Value for: {} Exception: {}", body, t.getMessage(), t);
            return "</>";
        }
    }


    @SuppressWarnings("unused")
    public static <T> List<T> toList(String stringValue, Class<T> clazz, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return Collections.emptyList();

        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
            return mapper.readValue(stringValue, javaType);
        } catch (IOException e) {
            try {
                return mapper.readValue(stringValue, new TypeReference<>() {
                });
            } catch (IOException ex) {
                return Collections.emptyList();
            }
        }
    }

    public static <T> Map<String, T> toMap(String stringValue, Class<T> tClass, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return new HashMap<>();

        if (Objects.isNull(tClass)) {
            try {
                //noinspection unchecked
                return mapper.readValue(stringValue, HashMap.class);
            } catch (IOException e) {
                return new HashMap<>();
            }
        }

        try {
            MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, tClass);
            return mapper.readValue(stringValue, mapType);
        } catch (IOException e) {
            try {
                //noinspection unchecked
                return mapper.readValue(stringValue, HashMap.class);
            } catch (IOException ex) {
                return new HashMap<>();
            }
        }
    }

    public static <T> T toObject(String stringValue, Class<T> tClass, ObjectMapper mapper) {
        if (StringUtils.isBlank(stringValue))
            return null;

        try {
            return mapper.readValue(stringValue, tClass);
        } catch (IOException e) {
            log.error("String Value: {} Deserialize to Object Exception: {}", stringValue, e.getMessage(), e);
            return null;
        }
    }
}