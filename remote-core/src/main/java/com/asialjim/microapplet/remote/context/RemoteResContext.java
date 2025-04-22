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
package com.asialjim.microapplet.remote.context;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
@Setter
public final class RemoteResContext {
    public static final GenericKey<Boolean> ENABLE_CALL_BACK = GenericKey.keyOf("enable_call_back");

    /**
     * 状态
     */
    private transient Object status;
    private transient Object protocol;

    /**
     * 响应头
     */
    private transient Object headers;

    /**
     * source
     */
    private transient Object source;

    /**
     * 临时相应结果
     */
    private transient Object tempData;

    /**
     * 相应结果
     */
    private transient Object data;

    /**
     * 此属性错误不需要被抛出
     */
    private transient Throwable cause;

    /**
     * 此属性表示需要抛出错误
     */
    private transient List<Throwable> throwable;
    private transient List<RemoteCallback> callbacks;
    private transient RemoteGenericMap genericMap;

    public <Value> void property(GenericKey<Value> genericKey, Value value) {
        if (Objects.nonNull(genericMap)) {
            genericMap.put(genericKey, value);
            return;
        }

        synchronized (RemoteResContext.class) {
            if (Objects.isNull(genericMap))
                genericMap = new RemoteGenericMap();
            genericMap.put(genericKey, value);
        }
    }

    public <Value> Value property(GenericKey<Value> genericKey) {
        if (Objects.isNull(this.genericMap))
            return null;

        //noinspection unchecked
        return (Value) genericMap.get(genericKey);
    }

    @SuppressWarnings("unused")
    public Collection<Object> values(){
        return Optional.ofNullable(this.genericMap).map(RemoteGenericMap::values).orElse(Collections.emptyList());
    }

    @SuppressWarnings("unused")
    public void addCallback(RemoteCallback callback) {
        if (Objects.nonNull(callbacks)) {
            callbacks.add(callback);
            return;
        }

        synchronized (RemoteResContext.class) {
            if (Objects.isNull(callbacks))
                callbacks = new ArrayList<>();

            callbacks.add(callback);
        }
    }

    public void callback() {
        Boolean enableCallBack = property(ENABLE_CALL_BACK);

        if (Boolean.TRUE.equals(enableCallBack) && Objects.nonNull(callbacks)) {
            log.info("Async Call Back");
            //noinspection TrivialFunctionalExpressionUsage
            ((Runnable) () -> callbacks.forEach(RemoteCallback::fun)).run();
        }
    }

    public void setThrowable(Throwable throwable) {
        if (Objects.isNull(this.throwable))
            this.throwable = new ArrayList<>();

        this.throwable.add(throwable);
    }
}