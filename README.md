# Remote
#### 介绍
    Remote 是一个声明式的 Java 流程处理框架
    基于此框架，任何业务流程均可以以注解+生命周期的模式，将该流程拆分，并组装到一个Java接口当中，且仅需要关心该流程处理的各自步骤即可，而不用关心该接口的实现。
    Remote 设计了一套回调接口，将Java流程处理当中的每一步细分到具体的回调接口中。用户可根据业务流程处理的实际情况，充分解耦该流程的每一步，最后利用Remote将这些步骤有机结合起来完成用户想要的业务逻辑
    当前 Remote 主要服务于利用 Remote 构建基于 HTTP/HTTPS 协议的三方公众平台 API 的客户端
    利用此客户端，用户可以像写 Feign 接口一样来调用三方系统的 API 并完成自己的业务逻辑处理
    当然， 我们依然可以基于 remote-core 带来的特性进行三方扩展，比如利用 remote-core 写一个从excel 表格中获取数据并按照一定规则将数据进行计算并进行其他业务操作。

#### 软件架构
    remote-core： Remote 框架核心架构，此库定义了Remote的一等公民：RemoteLifeCycle, LifeCycle, CallBack 基于此三个接口/注解，用户可以扩展任何想要的框架， 为此我们也希望参与者发挥你们的智慧，构建一个更加完整的 Remote 生态
    remote-net:   Remote 网络包，定义了与网络相关的部分库，利用 @Server 声明用户构建的客户端想要连接那个网络系统（服务器），并生命 Remote 网络环境仓库接口，用户可利用该接口完成对三方网络系统环境的管理，特别适用于在内网环境下，调用三方公众平台 API 域名被代理的情况
    remote-net-repository:    当前提供基于  mybatis-plus 的 基于 DB 的 Remote 网络环境仓库管理方案
    remote-net-http:   Remote  对 HTTP/HTTPS 协议下的 API 客户端适配，注意：当前库基于  netty  完成基于 HTTP/HTTPS 协议下对三方 HTTP/HTTPS 接口的访问功能
    remote-net-http-net:    基于netty的http网络请求框架
    remote-net-http-apache: 基于apache-http-client 的http网络请求框架
    remote-net-proxy:   网络代理框架
    remote-spring:    Remote 与 Spring 的集成
    remote-zen-pom:    提供一个类似于  spring-boot-dependencies 的版本管理库

#### 安装教程
```xml
<xml>
    <!--引入版本管理-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.github.microapplet</groupId>
                <artifactId>remote-zen-pom</artifactId>
                <version>${remote.version}</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!--按需要引入相关库如：构建 http 客户端引入-->
    <dependencies>
        <dependency>
            <groupId>io.gitee.asialjim</groupId>
            <artifactId>remote-net-http</artifactId>
        </dependency>
    </dependencies>
</xml>
```

#### 使用说明
```java
import java.io.Serializable;

// 不废话，直接上代码示例
public class WeChatAccessTokenTest {

    @Data
    public static class WeChatAccessTokenRes implements Serializable, Serializable {
        @java.io.Serial
        private static final long serialVersionUID = -3722640486042138636L;
        private Integer errcode;
        private String errmsg;
        private String access_token;
        private Integer expires_in;
    }

    @Server(schema = "https", host = "api.weixin.qq.com", port = 443)
    public interface WeChatAccessRemoting {
        @HttpMapping(method = HttpMethod.GET, uri = "/cgi-bin/token", queries = @HttpQuery(name = "grant_type", value = "client_credential"))
        WeChatAccessTokenRes accessToken(@HttpQuery(name = "appid") String appid, @HttpQuery(name = "secret") String secret);
    }

    WeChatAccessRemoting remoting;

    @Before
    public void before() {
        this.remoting = RemoteProxy.create(WeChatAccessRemoting.class);
    }

    @Test
    public void test() {
        WeChatAccessTokenRes weChatAccessTokenRes = remoting.accessToken("aaa", "bbb");
        System.out.println(weChatAccessTokenRes);    // 控制台输出： WeChatAccessTokenTest.WeChatAccessTokenRes(errcode=40013, errmsg=invalid appid rid: 64a171ca-1bdceef5-28207880, access_token=null, expires_in=null)
        Assert.assertNotNull(weChatAccessTokenRes);
        Assert.assertNotNull(weChatAccessTokenRes.getErrcode());
    }
}
```

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request