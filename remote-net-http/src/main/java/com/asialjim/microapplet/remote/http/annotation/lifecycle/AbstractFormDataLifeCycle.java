/*
 *  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
 package com.asialjim.microapplet.remote.http.annotation.lifecycle;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.http.annotation.body.FormData;
import com.asialjim.microapplet.remote.lifecycle.LogFunction;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import com.asialjim.microapplet.remote.lifecycle.callback.Invoke;
import com.asialjim.microapplet.remote.net.jackson.AbstractJacksonUtil;
import com.asialjim.microapplet.remote.net.mime.MimeMenu;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;

 public abstract class AbstractFormDataLifeCycle implements Before, Invoke {
     private static final Logger log = LoggerFactory.getLogger(AbstractFormDataLifeCycle.class);
     public static final GenericKey<List<RemoteMethodParameter>> FORM_DATA_CONFIG = GenericKey.keyOf("HTTP_FORM_DATA_CONFIG");
     public static final GenericKey<Boolean> FORM_DATA_REQUEST = GenericKey.keyOf("FORM_DATA_REQUEST");
     public static final GenericKey<List<UploadAttributeWrapper>> UPLOAD_ATTRIBUTE_LIST = GenericKey.keyOf("upload_attribute_list");
     public static final GenericKey<List<UploadByteArrayWrapper>> UPLOAD_CONTENT_LIST = GenericKey.keyOf("upload_content_list");
     public static final GenericKey<List<LogFunction>> FORM_LOG = GenericKey.keyOf("FORM_LOG");
     @Override
     public int order() {
         return Integer.MAX_VALUE - 1;
     }

     public static void callFromLog(RemoteReqContext req){
         List<LogFunction> functions = Optional.ofNullable(req.get(FORM_LOG)).orElseGet(ArrayList::new);
         functions.forEach(LogFunction::log);
     }

      private static void addFormData(RemoteMethodConfig methodConfig, boolean attribute, String mimeType, Class<?> clazz, Object body, String name, List<UploadAttributeWrapper> attributes, List<UploadByteArrayWrapper> contents, List<LogFunction> functions) {
         if (body instanceof List) {
             //noinspection rawtypes
             for (Object o :(List) body) {
                 if (Objects.isNull(o))
                     continue;
                 addFormData(methodConfig, attribute, mimeType, o.getClass(), o, name, attributes, contents, functions);
             }
             return;
         }

         if (body instanceof Map) {
             //noinspection unchecked
             Map<String, Object> map = (Map<String, Object>) body;
             for (Map.Entry<String, Object> entry : map.entrySet()) {
                 String childName = entry.getKey();
                 Object value = entry.getValue();
                 addFormData(methodConfig, attribute, mimeType, value.getClass(), value, childName, attributes, contents, functions);
             }
             return;
         }


         // 上传属性
         if (attribute || body instanceof String)
             addAttribute(methodConfig,mimeType, clazz, body, name, attributes, functions);
             // 上传二进制内容
         else
             addContent(methodConfig,body, name, contents,functions);
     }

     private static void addContent(RemoteMethodConfig methodConfig, Object body, String name, List<UploadByteArrayWrapper> contents, List<LogFunction> functions) {
         if (Objects.isNull(body))
             return;

         UploadByteArrayWrapper wrapper = body instanceof UploadByteArrayWrapper ? (UploadByteArrayWrapper) body : UploadByteArrayWrapper.create();

         if (body instanceof File) {
             wrapper.withContent((File) body);
         }
         if (body instanceof byte[]){
             wrapper.withContent((byte[]) body);
         }
         if (body instanceof MultipartFile){
             wrapper.withContent((MultipartFile) body);
         }
         if (body instanceof InputStream) {
             wrapper.withContent((InputStream) body);
         }

         wrapper.withName(name);
         functions.add(() -> log.info("\r\n\tRemote NET Req Form >>> Client:{} >>> {}", methodConfig.getRemoteName(),wrapper.logString()));
         contents.add(wrapper);
     }

     private static void addAttribute(RemoteMethodConfig methodConfig, String mimeType, Class<?> clazz, Object body, String name, List<UploadAttributeWrapper> attributes, List<LogFunction> functions) {
         if (StringUtils.isBlank(mimeType)) mimeType = "text/plain";
         String bodyStr;
         if (String.class.isAssignableFrom(clazz))
             bodyStr = (String) body;
         else if (Integer.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz))
             bodyStr = String.valueOf(body);
         else {
             ObjectMapper mapper;
             if (mimeType.contains(MimeMenu.JSON)) mapper = AbstractJacksonUtil.JSON_MAPPER;
             else if (mimeType.contains(MimeMenu.XML)) mapper = AbstractJacksonUtil.XML_MAPPER;
             else
                 throw new UnsupportedOperationException("不支持的媒体类型" + mimeType + ",当 FormData#attr 为 true 切参数类型不为基础类型时， FormData#mimeType 参数必须为 json 或者 xml");

             bodyStr = AbstractJacksonUtil.writeValueAsString(body, mapper);
         }
         UploadAttributeWrapper wrapper = UploadAttributeWrapper.builder().name(name).value(bodyStr).contentType(mimeType).build();
         functions.add(() -> log.info("\r\n\tRemote NET Req Form >>> Client:{} >>> Attribute: {}", methodConfig.getRemoteName(), wrapper));
         attributes.add(wrapper);
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         List<RemoteMethodParameter> parameters = methodConfig.config(FORM_DATA_CONFIG);
         if (CollectionUtils.isEmpty(parameters))
             return;

         List<UploadAttributeWrapper> attributes = req.get(UPLOAD_ATTRIBUTE_LIST);
         List<UploadByteArrayWrapper> contents = req.get(UPLOAD_CONTENT_LIST);
         if (Objects.isNull(attributes)) attributes = new ArrayList<>();
         if (Objects.isNull(contents)) contents = new ArrayList<>();
         List<LogFunction> functions = Optional.ofNullable(req.get(FORM_LOG)).orElseGet(ArrayList::new);
         for (RemoteMethodParameter parameter : parameters) {
             FormData annotation = parameter.getParameter().getAnnotation(FormData.class);
             String name = annotation.name();
             boolean attribute = annotation.attr();
             String mimeType = annotation.mimeType();
             int index = parameter.getIndex();
             Object body = args[index];
             Class<?> clazz = parameter.getClazz();

             addFormData(methodConfig,attribute, mimeType, clazz, body, name, attributes, contents, functions);
         }

         req.put(FORM_LOG,functions);
         req.put(UPLOAD_ATTRIBUTE_LIST, attributes);
         req.put(UPLOAD_CONTENT_LIST, contents);
         req.put(FORM_DATA_REQUEST,Boolean.TRUE);
         doBefore(data, methodConfig, req, res, args);
     }

     protected abstract void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args);
 }