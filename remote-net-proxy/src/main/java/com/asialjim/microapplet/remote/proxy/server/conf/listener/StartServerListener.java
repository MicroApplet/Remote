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
package com.asialjim.microapplet.remote.proxy.server.conf.listener;

import com.asialjim.microapplet.remote.proxy.server.ProxyServer;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

@Async
@Component
@EnableAsync
public class StartServerListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    private Set<ProxyServer> servers;
    @Resource
    private Executor executor;
    @Setter private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(@SuppressWarnings("NullableProblems") ContextRefreshedEvent event) {
        init();
        if (notPrepared())
            return;

        servers.forEach(item -> executor.execute(item::start));
    }

    private boolean notPrepared() {
        return Objects.isNull(servers) || servers.size() != 2;
    }

    private void init() {
        if (notPrepared()) {
            String[] names = applicationContext.getBeanNamesForType(ProxyServer.class);
            //noinspection ConstantConditions
            if (Objects.nonNull(names) && names.length == 2) {
                synchronized (StartServerListener.class) {
                    if (!notPrepared())
                        return;
                    servers = new HashSet<>(2);
                    for (String name : names) {
                        servers.add(applicationContext.getBean(name, ProxyServer.class));
                    }
                }
            }
        }
    }
}