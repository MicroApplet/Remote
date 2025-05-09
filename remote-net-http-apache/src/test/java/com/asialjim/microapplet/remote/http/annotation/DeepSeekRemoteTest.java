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

package com.asialjim.microapplet.remote.http.annotation;

import com.asialjim.microapplet.remote.http.annotation.meta.Message;
import com.asialjim.microapplet.remote.net.jackson.AbstractJacksonUtil;
import com.asialjim.microapplet.remote.net.lifecycle.callback.TextEventStreamResFun;
import com.asialjim.microapplet.remote.proxy.RemoteProxy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DeepSeekRemoteTest {

    AsialJimDifyRemoting remoting;

    @Before
    public void init() {
        remoting = RemoteProxy.create(AsialJimDifyRemoting.class);
    }

    @Test
    public void test() {

        Message msg = new Message();
        msg.setUser("abc");
        msg.setQuery("你好");
        msg.setResponse_mode("streaming");
        msg.withInput("a", "b");

        remoting.chat(msg, (TextEventStreamResFun) (reader, charset) -> {
            List<String> strings = IOUtils.readLines(reader);
            for (String string : strings) {
                String json = StringUtils.replaceOnce(string, "data: ", StringUtils.EMPTY);
                System.out.println(json);
//                Map<String, Object> map = AbstractJacksonUtil.json2map(json, Object.class);
//                System.out.println(map);
            }
        });
    }
}