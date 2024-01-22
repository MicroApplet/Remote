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
package io.github.microapplet.remote.net.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>前置系统支持的通讯协议 </h2>
 * <p>
 * <p/>Copyright &copy; Asial Jim Co., LTD
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0.0
 * @since 2022/8/29 , PROJECT-VERSION: 1.0.0,  JDK-VERSION: 8
 */
@Getter
public enum RemoteNetSchema {
    HTTP("HTTP","HTTPS","WSS"),
    GXP("GXP","ECIF","KAYAK"),
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