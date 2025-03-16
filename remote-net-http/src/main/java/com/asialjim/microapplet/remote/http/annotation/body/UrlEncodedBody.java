/*
 *  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.asialjim.microapplet.remote.http.annotation.body;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractEncodedBodyLifeCycle;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(UrlEncodedBody.EncodedBodyLifeCycle.class)
public @interface UrlEncodedBody {
    abstract class EncodedBodyLifeCycle extends AbstractEncodedBodyLifeCycle implements RemoteLifeCycle.LifeCycleHandler<UrlEncodedBody> {

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, UrlEncodedBody annotation) {
            methodConfig.config(URL_ENCODED_KEY, methodParameter);
        }
    }
}