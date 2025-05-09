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

// MessageRes.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.asialjim.microapplet.remote.http.annotation.meta;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class MessageRes implements Serializable {
    private String mode;
    private Metadata metadata;
    private String answer;
    private String conversation_id;
    private Long created_at;
    private String message_id;
    private String event;

    public static List<String> answer(MessageRes res){
        String s = Optional.ofNullable(res)
                .map(MessageRes::getAnswer)
                .orElse("服务繁忙，请稍后再试");
        return Collections.singletonList(s);
    }

    public static List<String> msg(MessageRes res){
        Stream<RetrieverResource> stream = Optional.ofNullable(res)
                .map(MessageRes::getMetadata)
                .map(Metadata::getRetriever_resources)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
        return stream.map(RetrieverResource::getContent).collect(Collectors.toList());
    }

    public static String conversation(MessageRes res){
        return Optional.ofNullable(res)
                .map(MessageRes::getConversation_id)
                .orElse(StringUtils.EMPTY);
    }
}

// Metadata.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

@Data
class Metadata implements Serializable{
    private List<RetrieverResource> retriever_resources;
    private Usage usage;
}

// RetrieverResource.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation


@Data
class RetrieverResource implements Serializable{
    private String document_name;
    private double score;
    private String dataset_id;
    private long position;
    private String dataset_name;
    private String document_id;
    private String segment_id;
    private String content;
}

// Usage.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

@Data
class Usage implements Serializable{
    private String completion_unit_price;
    private long completion_tokens;
    private long prompt_tokens;
    private String prompt_unit_price;
    private String prompt_price;
    private String total_price;
    private String completion_price_unit;
    private String prompt_price_unit;
    private double latency;
    private String completion_price;
    private long total_tokens;
    private String currency;
}
