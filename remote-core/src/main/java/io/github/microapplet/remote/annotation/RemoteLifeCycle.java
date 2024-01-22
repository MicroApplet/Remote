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
package io.github.microapplet.remote.annotation;

import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteMethodParameter;
import io.github.microapplet.remote.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.*;

/**
 * Remote 生命周期
 * <p> 用于标注该注解为一个流程处理生命周期注解
 * <br/> 一般注解被此注解标注，表示被此注解标注的注解在流程处理中的某个或者多个流程中具有对应的职责
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/10 &nbsp;&nbsp; 1.0 &nbsp;&nbsp; JDK 8
 */
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
