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

package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.Primary;
import com.asialjim.microapplet.remote.annotation.RemoteSubProperty;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.http.annotation.body.FormData;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.UploadAttributeWrapper;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.UploadByteArrayWrapper;
import com.asialjim.microapplet.remote.http.client.NettyRemoteHTTPClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Primary
@RemoteSubProperty("netty")
public class NettyFormDataLifeCycle extends FormData.FormDataLifeCycle {
    @Override
    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {

    }

    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        List<UploadAttributeWrapper> attributes = req.get(UPLOAD_ATTRIBUTE_LIST);
        List<UploadByteArrayWrapper> contents = req.get(UPLOAD_CONTENT_LIST);

        HttpRequest httpRequest = req.get(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY);
        if (Objects.isNull(httpRequest))
            httpRequest = NettyRemoteHTTPClient.buildHttpRequest(methodConfig, req);
        req.put(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY, httpRequest);

        try {
            HttpPostRequestEncoder postEncoder = new HttpPostRequestEncoder(httpRequest, true);
            if (CollectionUtils.isNotEmpty(attributes)) {
                for (UploadAttributeWrapper attribute : attributes) {
                    try {
                        MemoryAttribute memoryAttribute = new MemoryAttribute(attribute.getName(), attribute.getValue());
                        postEncoder.addBodyHttpData(memoryAttribute);
                    } catch (IOException e) {
                        log.warn("Remote NET Req Post Form-Data Add Attribute Exception: {}", e.getMessage(), e);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(contents)) {
                for (UploadByteArrayWrapper content : contents) {
                    try {
                        byte[] bytes = content.getContent();
                        Charset charset = StringUtils.isNotBlank(content.getCharset()) ? Charset.forName(content.getCharset()) : StandardCharsets.UTF_8;
                        MemoryFileUpload upload = new MemoryFileUpload(content.getName(), content.getFileName(), content.getContentType(), content.getContentTransferEncoding(), charset, bytes.length);
                        upload.setContent(Unpooled.wrappedBuffer(bytes));
                        postEncoder.addBodyHttpData(upload);
                    } catch (IOException e) {
                        log.warn("Remote NET Req Post Form-Data Add Content Exception: {}", e.getMessage(), e);
                    }
                }
            }


            httpRequest = postEncoder.finalizeRequest();
            HttpHeaders headers = httpRequest.headers();
            Map<String, String> header = Optional.ofNullable(req.get(HttpHeader.HttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
            headers.forEach(item -> header.put(item.getKey(),item.getValue()));
            req.put(HttpHeader.HttpHeaderLifeCycle.HTTP_HEADER_VALUE, header);
            req.put(NettyRemoteHTTPClient.HTTP_REQUEST_GENERIC_KEY,httpRequest);
            req.put(NettyRemoteHTTPClient.POST_REQUEST_ENCODER_GENERIC_KEY, postEncoder);
        } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
            res.setThrowable(e);
        }
    }


}