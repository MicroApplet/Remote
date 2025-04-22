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
package com.asialjim.microapplet.remote.net.mime;

import com.asialjim.microapplet.remote.context.GenericKey;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

@SuppressWarnings("unused")
public interface MimeMenu {
    String APPLICATION = "application";
    String AUDIO_TYPE = "audio";
    String IMAGE_TYPE = "image";
    String TEXT_TYPE = "text";
    String VIDEO_TYPE = "video";
    String WILDCARD = "*";
    String JSON = "json";
    String XML = "xml";
    String JAVASCRIPT = "javascript";

    MimeType ANY_TEXT = createConstant(TEXT_TYPE, WILDCARD);
    MimeType ANY_IMAGE = createConstant(IMAGE_TYPE, WILDCARD);
    MimeType ANY_AUDIO = createConstant(AUDIO_TYPE, WILDCARD);
    MimeType ANY_VIDEO = createConstant(VIDEO_TYPE, WILDCARD);
    MimeType APPLICATION_JSON = createConstant(APPLICATION, JSON);
    MimeType APPLICATION_XML = createConstant(APPLICATION, XML);
    MimeType TEXT_XML = createConstant(TEXT_TYPE, XML);
    MimeType APPLICATION_JAVASCRIPT = createConstant(APPLICATION, JAVASCRIPT);
    MimeType APPLICATION_BINARY = createConstant(APPLICATION, "binary");
    MimeType OCTET_STREAM = createConstant(APPLICATION, "octet-stream");
    MimeType PDF = createConstant(APPLICATION, "pdf");
    MimeType OOXML_SHEET = createConstant(APPLICATION, "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    MimeType OOXML_DOCUMENT = createConstant(APPLICATION, "vnd.openxmlformats-officedocument.wordprocessingml.document");
    MimeType OOXML_PRESENTATION = createConstant(APPLICATION, "vnd.openxmlformats-officedocument.presentationml.presentation");
    MimeType MICROSOFT_OUTLOOK = createConstant(APPLICATION, "vnd.ms-outlook");
    MimeType MICROSOFT_EXCEL = createConstant(APPLICATION, "vnd.ms-excel");
    MimeType MICROSOFT_POWERPOINT = createConstant(APPLICATION, "vnd.ms-powerpoint");
    MimeType MICROSOFT_WORD = createConstant(APPLICATION, "msword");

    String UNSUPPORT = "unsupport";
    MimeType UNSUPPORT_TYPE = createConstant(UNSUPPORT, UNSUPPORT);

    GenericKey<MimeType> MIME_TYPE_GENERIC_KEY = GenericKey.keyOf("mime_type_generic_key");

    static MimeType createConstant(String primaryType, String subType) {
        try {
            return new MimeType(primaryType, subType);
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e);
        }
    }

    static MimeType createConstant(String contentType) {
        try {
            return new MimeType(contentType);
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e);
        }
    }
}