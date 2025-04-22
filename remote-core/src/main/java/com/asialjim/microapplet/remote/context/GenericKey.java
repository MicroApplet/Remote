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

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@AllArgsConstructor
@SuppressWarnings("unused")
public class GenericKey<Value> {
    private final String key;

    public static<Value> GenericKey<Value> keyOf(String key){
        if (StringUtils.isBlank(key))
            throw new IllegalArgumentException("Key cannot be blank");
        return new GenericKey<>(key);
    }

    public String key(){
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !(o instanceof GenericKey))
            return false;

        return StringUtils.equals(this.key, ((GenericKey<?>)o).key);
    }

    @Override
    public String toString() {
        return "GenericKey{" + key + '}';
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}