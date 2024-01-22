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
package io.github.microapplet.remote.lifecycle;

import java.util.Comparator;

/**
 * 流程式处理生命周期
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/10 &nbsp;&nbsp; 1.0 &nbsp;&nbsp; JDK 8
 */
public interface LifeCycle extends Comparator<LifeCycle> {

    default int order() {
        return 0;
    }


    @Override
    default int compare(LifeCycle o1, LifeCycle o2) {
        return o1.order() - o2.order();
    }
}
