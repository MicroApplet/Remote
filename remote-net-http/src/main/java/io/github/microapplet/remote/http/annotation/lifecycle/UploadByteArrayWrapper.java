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
 package io.github.microapplet.remote.http.annotation.lifecycle;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Optional;


 /**
  * 文件上传包装器
  *
  * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
  * @version 4.0
  * @since 2023/7/10, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
  */
 @SuppressWarnings({"UnusedReturnValue", "unused"})
 @Data
 public class UploadByteArrayWrapper {
     private static final Logger log = LoggerFactory.getLogger(UploadByteArrayWrapper.class);
     private String name;
     private String fileName;
     private String contentType;
     private String contentTransferEncoding;
     private String charset;
     private byte[] content;

     public String logString(){
         int length = ArrayUtils.getLength(content);
         return "Upload Wrapper[name:" + name
                 + ",fileName:" + fileName
                 +",contentType:" + contentType
                 + ",contentTransferEncoding:" + contentTransferEncoding
                 + ",charset:" + charset
                 + ", size:" + length + "B  " + (length >> 10) + "kB  " + (length >> 10 >> 10) + "MB  " + (length >> 10 >> 10 >> 10) + "GB";
     }

     public byte[] getContent() {
         return Optional.ofNullable(this.content).orElse(new byte[0]);
     }

     public static UploadByteArrayWrapper create() {
         return new UploadByteArrayWrapper();
     }

     public UploadByteArrayWrapper withName(String name){
         this.name = name;
         return this;
     }

     public UploadByteArrayWrapper withFileName(String fileName) {
         this.fileName = fileName;
         this.contentType = contentType(fileName);
         return this;
     }

     public UploadByteArrayWrapper withContentType(String contentType) {
         this.contentType = contentType;
         return this;
     }

     public UploadByteArrayWrapper withContent(InputStream content) {
         try {
             this.content = IOUtils.toByteArray(content);
         } catch (IOException e) {
             log.error("Cannot parse ByteArray for InputStream: {}, Exception: {}", content, e.getMessage());
         }
         return this;
     }

     public UploadByteArrayWrapper withContent(byte[] content) {
         this.content = content;
         return this;
     }

     public UploadByteArrayWrapper withContent(File file) {
         try {
             this.content = FileUtils.readFileToByteArray(file);
             this.fileName = file.getName();
             this.contentType = contentType(fileName);
         } catch (IOException e) {
             log.error("Cannot parse ByteArray for File: {}, Exception: {}", file, e.getMessage());
         }
         return this;
     }

     public UploadByteArrayWrapper withContent(MultipartFile file){
         try {
             this.content = file.getBytes();
             this.fileName = file.getOriginalFilename();
             this.contentType = file.getContentType();
         } catch (IOException e) {
             log.error("Cannot parse ByteArray for MultipartFile: {}, Exception: {}", file, e.getMessage());
         }
         return this;
     }

     public UploadByteArrayWrapper withContentTransferEncoding(String contentTransferEncoding) {
         this.contentTransferEncoding = contentTransferEncoding;
         return this;
     }

     public UploadByteArrayWrapper withCharset(String charset) {
         this.charset = charset;
         return this;
     }

     public static String contentType(File file) {
         String fileName = Optional.ofNullable(file).map(File::getName).orElse(StringUtils.EMPTY);
         return contentType(fileName);
     }

     public static String contentType(String fileName) {
         if (StringUtils.isBlank(fileName))
             return "application/octet-stream";

         FileNameMap fileNameMap = URLConnection.getFileNameMap();
         return fileNameMap.getContentTypeFor(fileName);
     }
 }