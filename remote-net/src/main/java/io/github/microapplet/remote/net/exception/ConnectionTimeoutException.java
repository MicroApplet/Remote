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
package io.github.microapplet.remote.net.exception;

import lombok.AllArgsConstructor;

/**
 * <h1><em>ASIAL JIM JAVA DOC</em></h1><hr/>
 * <h2>CLASS DESCRIPTION <i>[ NAME: ConnectionTimeoutException ]</i> </h2><strong>
 * <p>
 * </strong><p><p>Copyright &copy; Asial Jim Co., LTD<hr/>
 *
 * @author Asial Jim &nbsp;&nbsp; <span>Email: &nbsp;&nbsp; <a href="mailto:asialjim@hotmail.com">asialjim@hotmail.com</a> &nbsp;&nbsp; <a href="asialjim@qq.com">asialjim@qq.com</a></span>
 * @version 1.0.0
 * @since 2022/7/27: 20:19   &nbsp;&nbsp; JDK 8
 */
@AllArgsConstructor
public final class ConnectionTimeoutException extends RuntimeException{
    private final String message;

    @SuppressWarnings("unused")
    public static void cast(String message){
        throw new ConnectionTimeoutException(message);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
