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
import com.asialjim.microapplet.remote.lifecycle.callback.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractOctetStreamBodyLifeCycle implements Before {
    public static final GenericKey<byte[]> OCTET_STREAM_VALUE = GenericKey.keyOf("OCTET_STREAM_VALUE");
    protected static final String METHOD_LOCATION = "method";
    protected static final String PARAMETER_LOCATION = "parameter";
    protected static final GenericKey<RemoteMethodParameter> OCTET_STREAM_CONFIG = GenericKey.keyOf("OCTET_STREAM_CONFIG");
    protected static final GenericKey<Map<String,Boolean>> OCTET_STREAM_PARAMETER_LOCATION = GenericKey.keyOf("OCTET_STREAM_PARAMETER_LOCATION");

    @Override
    public int order() {
        return Integer.MAX_VALUE - 100;
    }

    @Override
    public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        Map<String, Boolean> locationMap = Optional.ofNullable(methodConfig.config(OCTET_STREAM_PARAMETER_LOCATION)).orElseGet(HashMap::new);
        if (Boolean.TRUE.equals(locationMap.get(PARAMETER_LOCATION))) {
            RemoteMethodParameter parameter = methodConfig.config(OCTET_STREAM_CONFIG);
            if (Objects.isNull(parameter))
                return;

            int index = parameter.getIndex();
            Object body = args[index];
            byte[] bytes = byteBody(body);
            req.put(OCTET_STREAM_VALUE, bytes);
            doBefore(data, methodConfig, req, res, args);
        }

        if (Boolean.TRUE.equals(locationMap.get(METHOD_LOCATION))){
            doBefore(data, methodConfig, req, res, args);
        }
    }

    protected byte[] byteBody(Object body){
        if (Objects.isNull(body))
            return new byte[0];

        if (body instanceof byte[])
            return (byte[]) body;

        return new byte[0];
    }
    protected abstract void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args);
}