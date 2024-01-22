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

import io.github.microapplet.remote.context.*;
import io.github.microapplet.remote.lifecycle.callback.Before;

import java.lang.annotation.*;

/**
 * 可重复执行的
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/25, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(Retryable.RetryableLifeCycle.class)
public @interface Retryable {

    GenericKey<Boolean> RETRY_ABLE_KEY = GenericKey.keyOf("retry-able");

    final class RetryableLifeCycle implements RemoteLifeCycle.LifeCycleHandler<Retryable>, Before {
        @Override
        public int order() {
            return Integer.MIN_VALUE;
        }

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, Retryable annotation) {
            // do nothing here
        }

        @Override
        public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
            req.put(RETRY_ABLE_KEY, Boolean.TRUE);
        }
    }
}