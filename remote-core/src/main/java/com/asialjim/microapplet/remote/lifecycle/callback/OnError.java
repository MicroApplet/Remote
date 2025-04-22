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
package com.asialjim.microapplet.remote.lifecycle.callback;


import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import com.asialjim.microapplet.remote.lifecycle.CallBack;

@FunctionalInterface
public interface OnError extends CallBack {

    /**
     * 当流程出现异常时，回调此接口，并返回是否需要重试处理
     * @param data  相应结果
     * @param req   Remote 请求上下文
     * @param res   Remote 相应上下文
     */
    boolean onError(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Throwable ex, Object[] args);
}
