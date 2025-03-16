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
package com.asialjim.microapplet.remote.context;

import com.asialjim.microapplet.remote.lifecycle.LifeCycle;
import com.asialjim.microapplet.remote.lifecycle.callback.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RemoteHandlerContext {
    private static final Logger log = LoggerFactory.getLogger(RemoteHandlerContext.class);
    private transient final List<Before> beforeHandlers = new LinkedList<>();
    private transient final List<Invoke> invokeHandlers = new LinkedList<>();
    private transient final List<After> afterHandlers = new LinkedList<>();
    private transient final List<SuccessWhen> successWhenHandlers = new LinkedList<>();
    private transient final List<OnSuccess> onSuccessHandlers = new LinkedList<>();
    private transient final List<OnFail> onFailHandlers = new LinkedList<>();
    private transient final List<RetryWhen> retryWhenHandlers = new LinkedList<>();
    private transient final List<OnRetry> onRetryHandlers = new LinkedList<>();
    private transient final List<OnError> onErrorHandlers = new LinkedList<>();
    private transient final List<On> onHandlers = new LinkedList<>();
    private transient final List<Finally> finallyHandlers = new LinkedList<>();
    private transient final List<Integer> callbackIndex = new LinkedList<>();

    public void addCallBackIndex(Integer index) {
        if (Objects.isNull(index) || index < 0)
            return;
        this.callbackIndex.add(index);
    }

    public void addLifeCycle(LifeCycle lifeCycle) {
        if (Objects.isNull(lifeCycle))
            return;

        if (log.isDebugEnabled())
            log.debug("RemoteHandlerContext add LifeCycle: {} in...", lifeCycle.getClass().getSimpleName());

        if (lifeCycle instanceof Before) {
            this.beforeHandlers.add((Before) lifeCycle);
            this.beforeHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof Invoke) {
            this.invokeHandlers.add((Invoke) lifeCycle);
            this.invokeHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof After) {
            this.afterHandlers.add((After) lifeCycle);
            this.afterHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof SuccessWhen) {
            this.successWhenHandlers.add((SuccessWhen) lifeCycle);
            this.successWhenHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof OnSuccess) {
            this.onSuccessHandlers.add((OnSuccess) lifeCycle);
            this.onSuccessHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof OnFail) {
            this.onFailHandlers.add((OnFail) lifeCycle);
            this.onFailHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof RetryWhen) {
            this.retryWhenHandlers.add((RetryWhen) lifeCycle);
            this.retryWhenHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof OnRetry) {
            this.onRetryHandlers.add((OnRetry) lifeCycle);
            this.onRetryHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof OnError) {
            this.onErrorHandlers.add((OnError) lifeCycle);
            this.onErrorHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof On) {
            this.onHandlers.add((On) lifeCycle);
            this.onHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }

        if (lifeCycle instanceof Finally) {
            this.finallyHandlers.add((Finally) lifeCycle);
            this.finallyHandlers.sort(Comparator.comparingInt(LifeCycle::order));
        }
    }

    public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Integer index : callbackIndex) {
            if (index < 0)
                continue;

            Object arg = args[index];
            if (Objects.isNull(arg))
                continue;

            if (arg instanceof Before)
                ((Before) arg).before(data, methodConfig, req, res, args);
        }

        for (Before handler : beforeHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.before(data, methodConfig, req, res, args);
        }
    }


    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Integer index : callbackIndex) {
            Object arg = args[index];
            if (Objects.isNull(arg)) continue;

            if (arg instanceof Invoke)
                ((Invoke) arg).invoke(data, methodConfig, req, res, args);
        }

        for (Invoke handler : invokeHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.invoke(data, methodConfig, req, res, args);
        }
    }

    public void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Integer index : callbackIndex) {
            Object arg = args[index];
            if (Objects.isNull(arg)) continue;

            if (arg instanceof After) {
                ((After) arg).after(data, methodConfig, req, res, args);
            }
        }

        for (After handler : afterHandlers) {
            if (Objects.isNull(handler)) continue;
            handler.after(data, methodConfig, req, res, args);
        }
    }

    public void onSuccess(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {

        for (OnSuccess handler : onSuccessHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.onSuccess(data, methodConfig, req, res, args);
        }

        // 回调接口参数
        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack)) continue;

            if (callBack instanceof OnSuccess)
                ((OnSuccess) callBack).onSuccess(data, methodConfig, req, res, args);
        }
    }

    public void onFail(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (OnFail handler : onFailHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.onFail(data, methodConfig, req, res, args);
        }

        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack))
                continue;

            if (callBack instanceof OnFail) {
                ((OnFail) callBack).onFail(data, methodConfig, req, res, args);
            }
        }
    }

    public void onRetry(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {

        for (OnRetry handler : onRetryHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.onRetry(data, methodConfig, req, res, args);
        }

        // 回调接口参数
        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack))
                continue;

            if (callBack instanceof OnRetry) {
                ((OnRetry) callBack).onRetry(data, methodConfig, req, res, args);
            }
        }
    }

    public void on(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {

        for (On handler : onHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.on(data, methodConfig, req, res, args);
        }

        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack))
                continue;
            if (callBack instanceof On) {
                ((On) callBack).on(data, methodConfig, req, res, args);
            }
        }
    }

    public boolean retryWhen(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack)) continue;
            if (!(callBack instanceof RetryWhen)) continue;
            if (((RetryWhen) callBack).retryWhen(data, methodConfig, req, res, args))
                return true;
        }

        boolean retry = false;
        for (RetryWhen handler : retryWhenHandlers) {
            if (Objects.isNull(handler)) continue;
            if (handler.retryWhen(data, methodConfig, req, res, args)) {
                retry = true;
                break;
            }
        }
        return retry;
    }

    public boolean successWhen(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Integer index : this.callbackIndex) {
            Object callBack = args[index];
            if (Objects.isNull(callBack) || !(callBack instanceof SuccessWhen))
                continue;

            if (!((SuccessWhen) callBack).success(data, methodConfig, req, res, args))
                return false;
        }

        for (SuccessWhen handler : successWhenHandlers) {
            if (Objects.isNull(handler)) continue;
            if (!handler.success(data, methodConfig, req, res, args)) {
                return false;
            }
        }

        return true;
    }

    public boolean onError(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Throwable ex, Object[] args) {
        for (OnError handler : onErrorHandlers) {
            if (Objects.isNull(handler)) continue;
            if (handler.onError(data, methodConfig, req, res, ex, args))
                return true;
        }

        for (Integer index : this.callbackIndex) {
            if (index < 0) continue;

            Object callBack = args[index];
            if (Objects.isNull(callBack)) continue;

            if (callBack instanceof OnError) {
                if (((OnError) callBack).onError(data, methodConfig, req, res, ex, args))
                    return true;
            }
        }

        return false;
    }

    public void finalFunction(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        for (Finally handler : finallyHandlers) {
            if (Objects.isNull(handler))
                continue;
            handler.finallyFun(data, methodConfig, req, res, args);
        }

        for (Integer index : this.callbackIndex) {
            if (index < 0)
                continue;
            Object callBack = args[index];
            if (Objects.isNull(callBack))
                continue;

            if (callBack instanceof Finally) {
                ((Finally) callBack).finallyFun(data, methodConfig, req, res, args);
            }
        }
        req.clean();
    }

    @Override
    public RemoteHandlerContext clone() {
        try {
            return (RemoteHandlerContext) super.clone();
        } catch (Throwable t) {
            log.warn("Clone RemoteHandlerContext Exception:{}", t.getMessage(), t);
            return this;
        }
    }

    public List<Integer> callBackIndex() {
        return this.callbackIndex;
    }
}
