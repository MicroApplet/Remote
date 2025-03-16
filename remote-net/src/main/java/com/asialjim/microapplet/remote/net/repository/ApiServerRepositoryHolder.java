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
package com.asialjim.microapplet.remote.net.repository;

import com.asialjim.microapplet.remote.net.exception.DedicatedTimeoutException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ApiServerRepositoryHolder {
    private static final Logger log = LoggerFactory.getLogger(ApiServerRepositoryHolder.class);
    private static final Set<ApiServerRepository> HOLDER = new HashSet<>();

    /**
     * 添加网络通讯接口服务器配置信息长裤
	 * @param repository {@link ApiServerRepository repository}
     * @since 2022/12/10
     */
    public static void add(ApiServerRepository repository){
        if (Objects.isNull(repository))
            return;
        HOLDER.add(repository);
    }

    /**
     * 根据 API 供应商编号，业务编号以及环境编号，查询网络通讯接口服务器配置信息
     * @param supplier {@link String 供应商编号}
     * @param namespace {@link String 业务编号}
     * @param env {@link String 环境编号}
     * @return {@link ApiServerInfo 网络通讯接口服务器配置信息}
     * @since 2022/12/10
     */
    public static ApiServerInfo get(String supplier, String namespace, String env){
        ApiServerInfo info = null;
        for (ApiServerRepository repository : HOLDER) {
            info = repository.queryNetServerInfoBySupplierAndNamespaceAndEnv(supplier, namespace, env);
            break;
        }

        if (log.isDebugEnabled())
            log.debug("根据供应商：{}，业务：{}，环境：{}查询 API 服务器信息结果：{}", supplier,namespace,env,info);
        return info;
    }

    /**
     * 判断网络通讯接口服务器信息仓库
     */
    public static boolean hasRepositories(){
        return CollectionUtils.isNotEmpty(HOLDER);
    }

    public static void addTimeout(DedicatedTimeoutException exception) {
        if (Objects.isNull(exception))
            return;

        log.warn(exception.getMessage());
        for (ApiServerRepository repository : HOLDER) {
            repository.addTimeoutOnce(exception.getSupplier(),exception.getNamespace(),exception.getEnvironment());
        }
    }
}