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

import java.util.*;

public class RemoteGenericMap implements Map<GenericKey<?>,Object> {
    private final Map<GenericKey<?>, Object> map;

    public RemoteGenericMap() {
        this.map = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public RemoteGenericMap(Map<GenericKey<?>, Object> map) {
        this.map = Optional.ofNullable(map).orElseGet(HashMap::new);
    }

    public <Value> Value valueOf(GenericKey<Value> key){
        //noinspection unchecked
        return (Value) map.get(key);
    }

    public <Value> void valueOf(GenericKey<Value> key, Value value){
        map.put(key,value);
    }

    public <Value> Value valueOf(String key){
        GenericKey<Value> genericKey = GenericKey.keyOf(key);

        //noinspection unchecked
        return (Value) map.get(genericKey);
    }

    public <Value> void valueOf(String key, Value value){
        GenericKey<Value> genericKey = GenericKey.keyOf(key);
        map.put(genericKey,value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof GenericKey<?>)
            return map.containsKey(key);
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof GenericKey<?>)
            return map.get(key);
        return null;
    }

    @Override
    public Object put(GenericKey<?> key, Object value) {
        return map.put(key,value);
    }

    @Override
    public Object remove(Object key) {
        if (key instanceof GenericKey<?>)
            return map.remove(key);
        return null;
    }

    @Override
    public void putAll(@SuppressWarnings("NullableProblems") Map<? extends GenericKey<?>, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Set<GenericKey<?>> keySet() {
        return map.keySet();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Set<Entry<GenericKey<?>, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(";");

        map.forEach((key,value) -> sj.add(key.key() + "=" + value));
        return "GenericMap{" +
                "map=" + sj +
                '}';
    }
}