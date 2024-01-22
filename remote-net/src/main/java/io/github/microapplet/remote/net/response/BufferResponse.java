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
package io.github.microapplet.remote.net.response;

import javax.activation.MimeType;

/**
 * <h1><em>ASIAL JIM JAVA DOC</em></h1><hr/>
 * <h2>CLASS DESCRIPTION <i>[ NAME: BaseDownloadRes ]</i> </h2><strong>
 * <p> 文件下载基础包装类
 * </strong><p><p>Copyright &copy; Asial Jim Co., LTD<hr/>
 *
 * @author Asial Jim &nbsp;&nbsp; <span>Email: &nbsp;&nbsp; <a href="mailto:asialjim@hotmail.com">asialjim@hotmail.com</a> &nbsp;&nbsp; <a href="asialjim@qq.com">asialjim@qq.com</a></span>
 * @version 1.0.0
 * @since 2022/8/18: 16:42   &nbsp;&nbsp; JDK 8
 */
public interface BufferResponse {
    void setFileName(String fileName);

    void setContentType(String contentType);

    void setMimeType(MimeType mimeType);

    void setContentLength(Long contentLength);

    byte[] getBuffer();

    void setBuffer(byte[] buffer);
}