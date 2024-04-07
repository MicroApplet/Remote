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
package io.github.microapplet.remote.net.repository.mapper;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.github.microapplet.remote.net.repository.ApiServerInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@Table("t_rmt_net_svr")
public class ApiServerInfoPO implements Serializable {

    private static final long serialVersionUID = -1615501437940313064L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;

    /**
     * 供应商编号
     */
    private String sup;

    /**
     * 业务空间
     */
    private String svr;

    /**
     * 环境编号
     */
    private String envi;

    /**
     * 详细描述信息
     */
    @Column("r_desc")
    private String description;

    /**
     * 网络通讯协议
     */
    private String sch;

    /**
     * 主机名
     */
    @Column("r_host")
    private String host;

    /**
     * 通讯端口
     */
    @Column("r_port")
    private Integer port;

    /**
     * 代理主机名
     */
    @Column("p_host")
    private String proxyHost;

    /**
     * 代理端口
     */
    @Column("p_port")
    private Integer proxyPort;

    /**
     * 超时时间，单位：毫秒
     */
    @Column("r_time")
    private Integer rTime;

    /**
     * 字符集
     */
    @Column("r_char")
    private String rChar;

    @Column("l_env")
    private Integer lEnvi;

    private String arc;

    private Integer threshold;
    private Integer rNum;

    public ApiServerInfo apiServerInfo(){
        //  host 主机名为空表示没有数据                loop 表示空数据
        if (StringUtils.isBlank(this.getHost()))
            return null;

        ApiServerInfo info = new ApiServerInfo();
        info.setSupplier(this.sup);
        info.setNamespace(this.svr);
        info.setEnv(this.envi);
        info.setDescription(this.description);
        info.setSchema(this.sch);
        info.setHost(this.host);
        info.setPort(this.port);
        info.setProxyHost(this.proxyHost);
        info.setProxyPort(this.proxyPort);
        info.setTimeout(this.rTime);
        info.setCharset(this.rChar);
        info.setArc(this.arc);
        info.setThreshold(this.threshold);
        info.setNumber(this.rNum);
        return info;
    }
}