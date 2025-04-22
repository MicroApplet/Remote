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
package com.asialjim.microapplet.remote.annotation;

import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RemoteLifeCycle {

    Class<? extends LifeCycleHandler<?>>[] value();

    /**
     * 流程式处理生命周期
     *
     * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
     * @version 1.0
     * @since 2023/3/10, &nbsp;&nbsp; <em>version:1.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
     */
    interface LifeCycleHandler<A extends Annotation> extends LifeCycle {
        Logger log = LoggerFactory.getLogger(LifeCycleHandler.class);

        default void init(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, A annotation) {
            if (log.isDebugEnabled())
                log.debug("RemoteLifeCycleHandler: {} init method in...", methodConfig.getRemoteName());

            doInit(methodConfig, methodParameter, annotation);
        }

        /**
         * 初始化配置
         *
         * @param methodConfig    {@link RemoteMethodConfig methodConfig}
         * @param methodParameter {@link RemoteMethodParameter methodParameter}
         * @since 2023/3/10
         */
        void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, A annotation);
    }
}
