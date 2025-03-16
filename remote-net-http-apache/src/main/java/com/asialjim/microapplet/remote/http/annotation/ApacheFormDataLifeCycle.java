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
package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteSubProperty;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.http.annotation.body.FormData;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.UploadAttributeWrapper;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.UploadByteArrayWrapper;
import com.asialjim.microapplet.remote.http.client.ApacheRemoteHTTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RemoteSubProperty("apache")
public class ApacheFormDataLifeCycle extends FormData.FormDataLifeCycle {
    @Override
    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
    }

    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        List<UploadAttributeWrapper> attributes = Optional.ofNullable(req.get(UPLOAD_ATTRIBUTE_LIST)).orElseGet(ArrayList::new);
        List<UploadByteArrayWrapper> contents = Optional.ofNullable(req.get(UPLOAD_CONTENT_LIST)).orElseGet(ArrayList::new);
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        attributes.forEach(item -> builder.addTextBody(item.getName(), item.getValue(), ContentType.parse(item.getContentType())));
        contents.forEach(item -> {

            String fileName = item.getFileName();
            //noinspection deprecation
            String encode = URLEncoder.encode(fileName);
            builder.addBinaryBody(item.getName(),item.getContent(),ContentType.parse(item.getContentType()),encode);
        });
        //contents.forEach(item -> builder.addBinaryBody(item.getName(), item.getContent(), ContentType.parse(item.getContentType()), URLEncoder.encode(item.getFileName(), StandardCharsets.UTF_8.name()) item.getFileName()));

        HttpEntity entity = builder.build();
        req.put(ApacheRemoteHTTPClient.HTTP_ENTITY_GENERIC_KEY, entity);
    }
}