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
package com.asialjim.microapplet.remote.net.repository;

import com.asialjim.microapplet.remote.lifecycle.CallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;


public interface ApiServerRepository extends CallBack {
    Logger log = LoggerFactory.getLogger(ApiServerRepository.class);

    @PostConstruct
    default void init() {
        ApiServerRepositoryHolder.add(this);
        log.info("API Server Repository: [{}] init...", this.getClass().getName());
    }

    /**
     * 根据 API 供应商编号，业务编号以及环境编号，查询网络通讯接口服务器配置信息
     *
     * @param supplier  {@link String 供应商编号}
     * @param namespace {@link String 业务编号}
     * @param env       {@link String 环境编号}
     * @return {@link ApiServerInfo 网络通讯接口服务器配置信息}
     * @since 2022/12/10
     */
    ApiServerInfo queryNetServerInfoBySupplierAndNamespaceAndEnv(String supplier, String namespace, String env);

    /**
     * 添加超时次数
     *
     * @param supplierId  {@link String 供应商}
     * @param namespaceId {@link String 业务空间}
     * @param env         {@link String 网络环境}
     * @since 2023/3/17
     */
    void addTimeoutOnce(String supplierId, String namespaceId, String env);
}