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
package com.asialjim.microapplet.remote.net.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public enum RemoteNetSchema {
    HTTP("HTTP","HTTPS","WSS"),
    ;
    private final List<String> values;
    public static final CharSequence HTTP_STR = "HTTP";

    public static final String KAYAK_STR = "KAYAK";
    public static final String ECIF_STR = "ECIF";
    public static final String GXP_STR = "GXP";

    RemoteNetSchema(String... values) {
        if (Objects.isNull(values) || values.length == 0)
            this.values = new ArrayList<>(0);
        else
            this.values = Arrays.stream(values).collect(Collectors.toList());
    }
}