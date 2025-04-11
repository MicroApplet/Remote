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

package com.asialjim.microapplet.remote.spring;

import com.asialjim.microapplet.remote.proxy.RemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * Remote Bean 实例化工具
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/4/11, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Component
public class RemoteBeanPostProcessor implements InstantiationAwareBeanPostProcessor, PriorityOrdered {
    private static final Logger log = LoggerFactory.getLogger(RemoteBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInstantiation(@SuppressWarnings("NullableProblems") Class<?> beanClass,
                                                 @SuppressWarnings("NullableProblems") String beanName) throws BeansException {
        boolean candidateClass = RemoteBeanSelector.candidateClass(beanClass);
        if (!candidateClass)
            return null;
        if (log.isDebugEnabled())
            log.debug("Create Bean for Remote: {}", beanName);
        return RemoteProxy.create(beanClass);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}