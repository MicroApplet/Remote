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
package com.asialjim.microapplet.remote.lifecycle;

import com.asialjim.microapplet.remote.lifecycle.callback.OnSuccess;
import com.asialjim.microapplet.remote.lifecycle.callback.SuccessWhen;
import com.asialjim.microapplet.remote.proxy.RemoteProxy;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.UUID;

public class CallBackTest {

    interface CallBackInterface {
        void test(SuccessWhen successWhen, OnSuccess onSuccess);

        void before(com.asialjim.microapplet.remote.lifecycle.callback.Before before);
    }

    CallBackInterface callBackInterface;

    @Before
    public void before() {
        callBackInterface = RemoteProxy.create(CallBackInterface.class);
    }

    @Test
    public void test() {
        for (int i = 0; i < 5; i++) {
            MDC.put("REQUEST_ID", UUID.randomUUID().toString().toUpperCase());
            callBackInterface.test((data, methodConfig, req, res, args) -> {
                System.out.println("success when");
                return true;
            }, (data, methodConfig, req, res, args) -> System.out.println("on success"));

            System.out.println();
            MDC.clear();
        }
    }

    @Test
    public void beforeTest() {
        for (int i = 0; i < 5; i++) {
            callBackInterface.before((data, methodConfig, req, res, args) -> System.out.println("before"));
            System.out.println("==================================================================");

        }
    }
}