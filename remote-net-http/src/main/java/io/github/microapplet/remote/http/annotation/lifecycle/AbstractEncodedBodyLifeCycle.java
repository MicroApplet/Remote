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
 package io.github.microapplet.remote.http.annotation.lifecycle;

import io.github.microapplet.remote.context.*;
import io.github.microapplet.remote.lifecycle.callback.Before;
import io.github.microapplet.remote.lifecycle.callback.Invoke;
import io.github.microapplet.remote.net.constant.RemoteConstant;
import io.github.microapplet.remote.net.jackson.AbstractJacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static io.github.microapplet.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE;


 /**
  * URL-ENCODED 基础处理器
  *
  * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
  * @version 4.0
  * @since 2023/7/10, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
  */
 public abstract class AbstractEncodedBodyLifeCycle implements Before, Invoke {
     private static final Logger log = LoggerFactory.getLogger(AbstractEncodedBodyLifeCycle.class);
     public static final GenericKey<RemoteMethodParameter> URL_ENCODED_KEY = GenericKey.keyOf("URL_ENCODED_KEY");


     @Override
     public int order() {
         return Integer.MAX_VALUE - 100;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         Map<String, String> headers = Optional.ofNullable(req.get(HTTP_HEADER_VALUE)).orElse(new HashMap<>());
         String charsetName = Optional.ofNullable(req.get(RemoteConstant.CHARSET)).orElse(StandardCharsets.UTF_8.name());
         headers.put("Content-Type", "application/x-www-form-urlencoded; charset=" + charsetName);
         req.put(HTTP_HEADER_VALUE, headers);
         try {
             // REQUEST_BODY_KEY
             RemoteMethodParameter parameter = methodConfig.config(URL_ENCODED_KEY);
             Object bodyObj = args[parameter.getIndex()];
             String url_encoded;
             if (bodyObj instanceof String) {
                 url_encoded = (String) bodyObj;
             }
             else if (bodyObj instanceof Map){
                 StringJoiner sj = new StringJoiner("&");
                 //noinspection unchecked
                 Map<String,Object> map = (Map<String, Object>) bodyObj;
                 map.forEach((key,value) -> sj.add(key + "=" + value));
                 url_encoded = sj.toString();
             } else {
                 StringJoiner sj = new StringJoiner("&");
                 String json = AbstractJacksonUtil.writeValueAsString(bodyObj,AbstractJacksonUtil.JSON_MAPPER);
                 Map<String, String> map = AbstractJacksonUtil.toMap(json,String.class, AbstractJacksonUtil.JSON_MAPPER);
                 map.forEach((key, value) -> sj.add(key + "=" + value));
                 url_encoded = sj.toString();
             }

             req.put(BaseObjectMapperRequestBodyLifeCycle.STRING_BODY_KEY, url_encoded);
             doBefore(data, methodConfig, req, res, args);
         } catch (Throwable t) {
             res.setCause(t);
         }
     }

     @Override
     public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         String urlEncoded = req.get(BaseObjectMapperRequestBodyLifeCycle.STRING_BODY_KEY);
         log.info("\r\n\tRemote NET Req Body >>> {}", urlEncoded);
     }

     protected abstract void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args);
 }