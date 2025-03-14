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
package io.github.microapplet.remote.net.repository;

import lombok.Data;

import java.io.Serializable;

/**
 * 网络通讯接口服务器信息 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2022/12/10, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Data
public class ApiServerInfo implements Serializable {
    private static final long serialVersionUID = 3244033039884864105L;
    public static final String LOOP = "loop";

    /**
     * 供应商编号
     */
    private String supplier;

    /**
     * 业务空间
     */
    private String namespace;

    /**
     * 环境编号
     */
    private String env;
    /** 本地环境编号*/
    private Integer lEnvi;

    /**
     * 详细描述信息
     */
    private String description;

    /**
     * 网络通讯协议
     */
    private String schema;

    /**
     * 主机名
     */
    private String host;

    /**
     * 通讯端口
     */
    private Integer port;

    /**
     * 代理主机名
     */
    private String proxyHost;

    /**
     * 代理端口
     */
    private Integer proxyPort;

    /**
     * 超时时间，单位：毫秒
     */
    private Integer timeout;

    /**
     * 字符集
     */
    private String charset;

    private String arc;

    private Integer threshold;
    private Integer number;
}