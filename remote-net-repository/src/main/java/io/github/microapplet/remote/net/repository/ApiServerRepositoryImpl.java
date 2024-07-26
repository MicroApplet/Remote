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

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryWrapperAdapter;
import io.github.microapplet.remote.config.RemoteLocalEnvironment;
import io.github.microapplet.remote.net.event.ApiServerEnvironmentLockedEvent;
import io.github.microapplet.remote.net.repository.mapper.ApiServerInfoMapper;
import io.github.microapplet.remote.net.repository.mapper.ApiServerInfoPO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

/**
 * Remote  网络仓库管理
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/18, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@Component
public class ApiServerRepositoryImpl implements ApiServerRepository, ApplicationContextAware {
    private static final String PREFIX = "RMT:NET:%s";
    private static final String TEMPLATE = "%s:%s:%s:%s:%s";
    @Resource
    private RemoteLocalEnvironment localEnvironment;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ApiServerInfoMapper mapper;

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public ApiServerInfo queryNetServerInfoBySupplierAndNamespaceAndEnv(String supplier, String namespace, String env) {
        int localEnv = localEnvironment.localEnv().getCode();
        String arch = localEnvironment.arch().name();
        String key = String.format(TEMPLATE, supplier, namespace, env, arch, localEnv);
        String lockKey = key + ":lock";

        // 环境已锁定
        if (Objects.nonNull(redisTemplate.opsForValue().get(lockKey))) {
            log.info("查询API服务器配置信息，条件[供应商:{};业务:{};环境：{};本地环境：{}; 系统架构：{}]， 已锁定, 系统将自动启用公网环境", supplier, namespace, env, localEnv, arch);
            // 使用公网环境
            env = "NET";
        }
        ApiServerInfo apiServerInfo = doQueryNetServerInfoBySupplierAndNamespaceAndEnv(supplier, namespace, env, arch, localEnv);
        if (StringUtils.equals(ApiServerInfo.LOOP, Optional.ofNullable(apiServerInfo).map(ApiServerInfo::getHost).orElse(StringUtils.EMPTY)))
            return null;
        return apiServerInfo;
    }

    @Override
    public void addTimeoutOnce(String supplierId, String namespaceId, String env) {
        if (StringUtils.equalsAnyIgnoreCase("NET", env)) {
            log.info("公网超时 ：{}:{}:{}", supplierId, namespaceId, env);
            return;
        }

        log.info("添加一次超时次数 ：{}:{}:{}", supplierId, namespaceId, env);
        ApiServerInfo apiServerInfo = queryNetServerInfoBySupplierAndNamespaceAndEnv(supplierId, namespaceId, env);
        if (Objects.isNull(apiServerInfo)) {
            log.info("未找到要添加超时次数的网络环境信息,不再添加次数");
            return;
        }

        String key = String.format(TEMPLATE, supplierId, namespaceId, env, localEnvironment.arch().name(), localEnvironment.localEnv().getCode());
        String lockKey = key + ":lock";
        Object lock = redisTemplate.opsForValue().get(lockKey);
        if (log.isDebugEnabled())
            log.info("网络环境：{}：{}：{}锁定情况：{}", supplierId, namespaceId, env, lock);

        if ("lock".equals(lock)) {
            log.info("网络环境：{}:{}:{}已锁定,不需要添加超时次数", supplierId, namespaceId, env);
            return;
        }

        // 当前系统时间
        long currentTimeMillis = System.currentTimeMillis();

        // 单位： 分钟
        Integer maxTimeoutThreshold = apiServerInfo.getThreshold();
        Integer maxTimeoutNumber = apiServerInfo.getNumber();
        if (Objects.isNull(maxTimeoutNumber))
            throw new IllegalStateException("三方API网络系统组件,【供应商:" + supplierId + ",命名空间:" + namespaceId + ",网络环境:" + env + "】,未设置阈值超时次数");

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(currentTimeMillis);
        current.add(Calendar.MINUTE, maxTimeoutThreshold);

        // 获取当前超时记录过期时间
        // z_set 分数： 当前系统时间 + 滑动窗口时间
        long expiresTime = current.getTimeInMillis();
        log.info("网络环境：{}：{}：{}超时记录过期时间：{}", supplierId, namespaceId, env, expiresTime);

        String currentCalendarStr = current.get(Calendar.YEAR) + "/" + current.get(Calendar.MONTH) + "/" + current.get(Calendar.DAY_OF_MONTH) + ";" + current.get(Calendar.HOUR_OF_DAY) + ":" + current.get(Calendar.MINUTE) + ":" + current.get(Calendar.SECOND);

        // 添加超时记录
        Boolean add = redisTemplate.opsForZSet().add(key, currentCalendarStr, expiresTime);
        log.info("网络环境：{}：{}：{}添加超时次数结果：{}", supplierId, namespaceId, env, add);

        // 清除 z_set 分数小于当前系统时间
        // 因为在添加超时时间时，指定的 z_set 分数规则为： 当前系统时间 + 滑动窗口时间
        Long aLong = redisTemplate.opsForZSet().removeRangeByScore(key, 0, currentTimeMillis);
        log.info("清除网络环境：{}：{}：{}过期的超时记录次数：{}", supplierId, namespaceId, env, aLong);

        // 获取从现在往倒数  maxTimeoutThreshold 分钟以前的超时次数
        Long count = redisTemplate.opsForZSet().count(key, currentTimeMillis, expiresTime);
        log.info("网络环境：{}：{}：{}滑动窗口时间内，超时次数为：{}", supplierId, namespaceId, env, count);

        // 设置过期时间为7天以后
        // 设置滑动窗口时间长度不能超过7天
        current.setTimeInMillis(currentTimeMillis);
        current.add(Calendar.DAY_OF_YEAR, 7);
        redisTemplate.expireAt(key, current.getTime());

        // 未达到阈值，直接返回
        if (Objects.isNull(count) || count.intValue() < maxTimeoutNumber)
            return;

        // 为API 服务器网络环境上锁
        // API 服务器网络环境锁不过期
        redisTemplate.opsForValue().set(lockKey, "lock");

        // 达到超时次数阈值, 向指定用户发送消息通知
        applicationContext.publishEvent(new ApiServerEnvironmentLockedEvent(apiServerInfo));
    }


    private ApiServerInfo doQueryNetServerInfoBySupplierAndNamespaceAndEnv(String supplier, String namespace, String env, String arch, int localEnv) {
        // 获取缓存
        String cacheKey = String.format(PREFIX, String.format(TEMPLATE, supplier, namespace, env, arch, localEnv));
        ApiServerInfoPO po = (ApiServerInfoPO) redisTemplate.opsForValue().get(cacheKey);

        Optional<ApiServerInfo> apiServerInfoOpt = Optional.ofNullable(po).map(ApiServerInfoPO::apiServerInfo);
        // 缓存中有数据, 直接返回
        if (apiServerInfoOpt.isPresent())
            return apiServerInfoOpt.get();
        QueryWrapper wrapper = QueryWrapperAdapter.create()
                .where(ApiServerInfoPO::getSup).eq(supplier)
                .where(ApiServerInfoPO::getSvr).eq(namespace)
                .where(ApiServerInfoPO::getEnvi).eq(env)
                .where(ApiServerInfoPO::getLEnvi).eq(localEnv)
                .where(ApiServerInfoPO::getArc).eq(arch);

        // 从数据库中获取数据
        po = mapper.selectOneByQuery(wrapper);
        apiServerInfoOpt = Optional.ofNullable(po).map(ApiServerInfoPO::apiServerInfo);

        // 数据库中存在有效数据
        if (apiServerInfoOpt.isPresent()) {
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, po, Duration.ofHours(12));
            return apiServerInfoOpt.get();
        }

        log.info("找不到API服务器配置信息,条件[供应商:{};业务:{};环境：{};本地环境：{}; 系统架构：{}]", supplier, namespace, env, localEnv, arch);
        // 向 缓存存入空数据
        po = new ApiServerInfoPO();
        po.setHost(ApiServerInfo.LOOP);
        // 空数据缓存 10 分钟
        redisTemplate.opsForValue().set(cacheKey, po, Duration.ofMinutes(1));

        if (localEnv > 0) {
            // 递归获取更高级的本地环境配置
            ApiServerInfo apiServerInfo = doQueryNetServerInfoBySupplierAndNamespaceAndEnv(supplier, namespace, env, arch, localEnv - 1);
            if (Objects.nonNull(apiServerInfo))
                return apiServerInfo;
        }

        return po.apiServerInfo();
    }
}