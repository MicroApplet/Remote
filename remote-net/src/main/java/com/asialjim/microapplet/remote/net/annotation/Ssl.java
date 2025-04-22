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
package com.asialjim.microapplet.remote.net.annotation;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(Ssl.SslLifeCycle.class)
public @interface Ssl {

    abstract class SslLifeCycle extends AbstractSslLifeCycle implements RemoteLifeCycle.LifeCycleHandler<Ssl>{

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, Ssl annotation) {
            methodConfig.config(SSL_PARAMETER_KEY, methodParameter);
        }
    }
}