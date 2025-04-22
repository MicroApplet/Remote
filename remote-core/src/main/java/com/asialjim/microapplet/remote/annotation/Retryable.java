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

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;

import java.lang.annotation.*;

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