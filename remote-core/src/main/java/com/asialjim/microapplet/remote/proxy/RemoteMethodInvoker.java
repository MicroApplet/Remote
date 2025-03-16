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
package com.asialjim.microapplet.remote.proxy;

import com.asialjim.microapplet.remote.annotation.Retryable;
import com.asialjim.microapplet.remote.context.RemoteHandlerContext;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteReqContext;
import com.asialjim.microapplet.remote.context.RemoteResContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.*;

public class RemoteMethodInvoker {
    private static final Logger log = LoggerFactory.getLogger(RemoteMethodInvoker.class);

    private final RemoteMethodConfig methodConfig;

    public RemoteMethodInvoker(RemoteMethodConfig methodConfig) {
        this.methodConfig = methodConfig;
    }

    public Object invoke(Object[] args) throws Throwable {
        String trace = MDC.get("REQUEST_ID");
        if (StringUtils.isBlank(trace))
            trace = UUID.randomUUID().toString().substring(0,8);
        MDC.put("REQUEST_ID", trace);
        RemoteReqContext reqContext = new RemoteReqContext();
        RemoteResContext resContext = new RemoteResContext();
        Object[] copyArgs = Objects.isNull(args) ? new Object[0] : args.clone();

        RemoteHandlerContext handlerContext = this.methodConfig.getRemoteHandlerContext();

        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            Object o = doInvoke(handlerContext, this.methodConfig, reqContext, resContext, copyArgs);
            stopWatch.stop();
            return o;
        } finally {
            long time = stopWatch.getTime();
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 on 方法", methodConfig.getRemoteName());

            handlerContext.on(resContext.getData(), this.methodConfig, reqContext, resContext, copyArgs);
            log.info("\r\n\tRemote 客户端：{} 执行时间： {} 毫秒\r\n", methodConfig.getRemoteName(), time);
        }
    }

    private Object doInvoke(RemoteHandlerContext handlerContext, RemoteMethodConfig methodConfig, RemoteReqContext reqContext, RemoteResContext resContext, Object[] copyArgs) {
        if (reqContext.retryTimes() > 0)
            log.info("Remote 客户端: {} 调用开始, 重试次数: {}...", methodConfig.getRemoteName(), reqContext.retryTimes());

        reqContext.addRetryTimes();
        if (reqContext.retryTimes() > 10)
            throw new RuntimeException("达到最大重试次数：10， 你必须自定义最大重试次数，且要求小于10次");

        try {
            // before方法
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 before 方法", methodConfig.getRemoteName());
            handlerContext.before(resContext.getData(), methodConfig, reqContext, resContext, copyArgs);

            // 执行方法
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 invoke 方法", methodConfig.getRemoteName());
            handlerContext.invoke(resContext.getData(), methodConfig, reqContext, resContext, copyArgs);

            // after 方法
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 after 方法", methodConfig.getRemoteName());
            handlerContext.after(resContext.getData(), methodConfig, reqContext, resContext, copyArgs);
            Object data = resContext.getData();

            // 判断流程是否错误
            if (Objects.nonNull(resContext.getCause())) {
                log.error("Remote 客户端： {} 执行异常：{}", methodConfig.getRemoteName(), resContext.getCause().getMessage(),resContext.getCause());
                // 抛出错误，并交由 catch 模块中的回调函数处理
                throw resContext.getCause();
            }
            // 判断流程是否成功
            if (log.isDebugEnabled())
                log.debug("Remote 客户端： {} 执行 successWhen 方法", methodConfig.getRemoteName());
            boolean success = handlerContext.successWhen(data, methodConfig, reqContext, resContext, copyArgs);
            // 判定业务成功， 返回类型不为 void， 且返回结果不为空
            if (success && !Void.class.isAssignableFrom(methodConfig.getReturnClass()) && Objects.nonNull(data)) {
                // 回调成功函数
                if (log.isDebugEnabled())
                    log.debug("Remote 客户端： {} 执行 onSuccess 方法", methodConfig.getRemoteName());
                handlerContext.onSuccess(data, methodConfig, reqContext, resContext, copyArgs);
                return data;
            }

            // 回调失败函数
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 onFail 方法", methodConfig.getRemoteName());
            handlerContext.onFail(data, methodConfig, reqContext, resContext, copyArgs);

            Boolean retryable = Optional.ofNullable(reqContext.get(Retryable.RETRY_ABLE_KEY)).orElse(Boolean.FALSE);

            // 判断是否需要重试
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 retryWhen 方法", methodConfig.getRemoteName());
            boolean retry = retryable && handlerContext.retryWhen(data, methodConfig, reqContext, resContext, copyArgs);

            // 不需要重试，直接返回
            if (!retry) return data;

            // 需要重试,回调重试回调函数
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 onRetry 方法", methodConfig.getRemoteName());
            handlerContext.onRetry(data, methodConfig, reqContext, resContext, copyArgs);

            // 重试
            return doInvoke(handlerContext, methodConfig, reqContext, resContext, copyArgs);
        } catch (Throwable t) {
            Boolean retryable = Optional.ofNullable(reqContext.get(Retryable.RETRY_ABLE_KEY)).orElse(Boolean.FALSE);

            // 回调异常回调函数
            if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 onError 方法", methodConfig.getRemoteName());
            boolean retry = handlerContext.onError(resContext.getData(), methodConfig, reqContext, resContext, t, copyArgs) && retryable;

            // 需要重试
            if (retry) {
                if (log.isDebugEnabled()) log.debug("Remote 客户端： {} 执行 onRetry 方法", methodConfig.getRemoteName());
                handlerContext.onRetry(resContext.getData(), methodConfig, reqContext, resContext, copyArgs);
                // 重试
                return doInvoke(handlerContext, methodConfig, reqContext, resContext, copyArgs);
            }

            if (CollectionUtils.isNotEmpty(resContext.getThrowable())){
                List<Throwable> throwable = resContext.getThrowable();
                StringJoiner sj = new StringJoiner(";\r\n");
                for (Throwable e : throwable) {
                    sj.add(e.getMessage());
                }
                log.error("Remote 客户端： {} 执行异常：{}", methodConfig.getRemoteName(), sj);
                throw new RuntimeException(sj.toString());
            }

            return resContext.getData();
        } finally {
            handlerContext.finalFunction(resContext.getData(),methodConfig,reqContext,resContext,copyArgs);
        }
    }
}