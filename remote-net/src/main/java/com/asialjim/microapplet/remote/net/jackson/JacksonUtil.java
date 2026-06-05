/*
 *    Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.asialjim.microapplet.remote.net.jackson;

import org.apache.commons.lang3.Strings;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * 基于jackson的工具
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/8/7, &nbsp;&nbsp; <em>version:1.0</em>
 */
@SuppressWarnings("unused")
public abstract class JacksonUtil<M extends ObjectMapper, B extends MapperBuilder<M, B>> {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Shanghai"));
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
    protected final MapperBuilder<M, B> builder;
    protected final M mapper;

    public JacksonUtil(MapperBuilder<M, B> builder) {
        this.builder = builder;
        this.mapper = builder.build();
    }


    public M objectMapper() {
        return this.mapper;
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> JacksonUtil<M, B> instance(MapperBuilder<M, B> builder) {
        return new JacksonUtil<>(builder) {
        };
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> MapperBuilder<M, B> init(MapperBuilder<M, B> mapper) {
        if (Objects.isNull(mapper)) //noinspection unchecked
            return (MapperBuilder<M, B>) JsonMapper.builder();

        // 配置序列化特性
        configureSerializationFeatures(mapper);

        // 配置反序列化特性
        configureDeserializationFeatures(mapper);

        // 配置日期时间处理
        configureDateTimeHandling(mapper);

        // 配置序列化包含规则
        configureSerializationInclusion(mapper);
        return mapper;
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> void configureSerializationFeatures(MapperBuilder<M, B> mapper) {
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> void configureDeserializationFeatures(MapperBuilder<M, B> mapper) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> void configureDateTimeHandling(MapperBuilder<M, B> mapper) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        // 设置传统日期格式
        mapper.defaultTimeZone(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);
        mapper.defaultDateFormat(simpleDateFormat);


        JacksonModule[] modules = getModules();
        mapper.addModules(modules);
    }

    public static JacksonModule[] getModules() {
        return new JacksonModule[]{javaTimeModule()};
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> JacksonModule javaTimeModule() {

        // 创建自定义模块
        SimpleModule module = new SimpleModule("CustomJavaTimeModule");
        module.addSerializer(new LocalDateTimeSerializer(dateTimeFormatter));
        module.addSerializer(new LocalDateSerializer(dateFormatter));
        module.addSerializer(new LocalTimeSerializer(timeFormatter));

        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        return module;
    }

    public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> void configureSerializationInclusion(MapperBuilder<M, B> mapper) {
        // 注意：setSerializationInclusion 会覆盖前一个设置
        // 所以合并为一次设置，优先使用 NON_EMPTY
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }


    public final JavaType constructType(Type type) {
        return objectMapper().getTypeFactory().constructType(type);
    }

    public final JavaType constructParameterizedType(Class<?> rawType, Class<?>... args) {
        return objectMapper().getTypeFactory().constructParametricType(rawType, args);
    }

    public final String toStr(Object bean) {
        return objectMapper().writeValueAsString(bean);
    }

    public final <T> T toBean(byte[] is, JavaType type) {
        return objectMapper().readValue(is, type);
    }

    public final <T> T toBeanParameterizedType(byte[] is, Class<T> rawType, Class<?>... args) {
        JavaType type = constructParameterizedType(rawType, args);
        return objectMapper().readValue(is, type);
    }

    public final <T> T toBean(InputStream is, JavaType type) {
        return objectMapper().readValue(is, type);
    }

    public final <T> T toBeanParameterizedType(InputStream is, Class<T> rawType, Class<?>... args) {
        JavaType type = constructParameterizedType(rawType, args);
        return objectMapper().readValue(is, type);
    }

    public final <T> T toBean(InputStream is, Class<T> type) {
        return objectMapper().readValue(is, type);
    }

    public final <T> T toBean(String str, Class<T> type) {
        if (Strings.CI.startsWith(str, "\"") && Strings.CI.endsWith(str, "\""))
            str = objectMapper().readValue(str, String.class);
        return objectMapper().readValue(str, type);
    }

    public final <T> T toBean(String str, TypeReference<T> type) {
        if (Strings.CI.startsWith(str, "\"") && Strings.CI.endsWith(str, "\""))
            str = objectMapper().readValue(str, String.class);

        return objectMapper().readValue(str, type);
    }

    public final <T> T toBean(String str, JavaType javaType) {
        if (Strings.CI.startsWith(str, "\"") && Strings.CI.endsWith(str, "\""))
            str = objectMapper().readValue(str, String.class);

        return objectMapper().readValue(str, javaType);
    }

    public final <T> Map<String, T> toMap(String str, Class<T> valueType) {
        if (Strings.CI.startsWith(str, "\"") && Strings.CI.endsWith(str, "\""))
            str = objectMapper().readValue(str, String.class);

        JavaType javaType = constructParameterizedType(Map.class, String.class, valueType);
        return toBean(str, javaType);
    }

    public final <T> List<T> toList(String str, Class<T> listType) {
        if (Strings.CI.startsWith(str, "\"") && Strings.CI.endsWith(str, "\""))
            str = objectMapper().readValue(str, String.class);

        JavaType javaType = constructParameterizedType(List.class, listType);
        return toBean(str, javaType);
    }
}