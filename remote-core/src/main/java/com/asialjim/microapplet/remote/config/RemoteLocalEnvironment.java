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
package com.asialjim.microapplet.remote.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

public interface RemoteLocalEnvironment {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    enum RemoteLocalEnv {
        PROD("PROD", 0, "生产环境"),
        GARY("GARY", 1, "灰度环境"),
        TEST("TEST", 2, "测试环境"),
        DEVE("DEVE", 3, "开发环境"),
        DEFT("DEFT", 4, "默认环境"),
        NONE("_NONE", -1, "空环境，找不到环境");

        private final String name;
        private final int code;
        private final String description;

        public static RemoteLocalEnv nameOf(String value){
            return Arrays.stream(values()).filter(item -> StringUtils.equalsIgnoreCase(value, item.name())).findFirst().orElse(NONE);
        }

        @SuppressWarnings("unused")
        public static int envCode(String name) {
            if (StringUtils.isBlank(name))
                return DEFT.getCode();

            return Arrays.stream(values()).filter(item -> name.equals(item.name)).findFirst().orElse(DEFT).getCode();
        }
    }

    @SuppressWarnings("unused")
    enum Arch{
        OTHER, X86,ARM;

        public static Arch nameOf(String value){
            return Arrays.stream(values()).filter(item -> StringUtils.equalsIgnoreCase(value, item.name())).findFirst().orElse(OTHER);
        }

    }

    /**
     * 获取当前系统的本地环境信息
     * @return {@link RemoteLocalEnv 本地环境}
     * @since 2023/3/17
     */
    default RemoteLocalEnv localEnv(){
        RemoteLocalEnv remoteLocalEnv = doLocalEnv();
        if (Objects.nonNull(remoteLocalEnv))
            return remoteLocalEnv;

        return RemoteLocalEnv.DEFT;
    }

    RemoteLocalEnv doLocalEnv();

    /**
     * 获取当前系统架构
     * @return {@link Arch 当前运行环境系统架构}
     * @since 2023/3/17
     */
    Arch arch();
}