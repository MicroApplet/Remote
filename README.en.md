# Remote
#### Description
    "Remote" is a declarative Java workflow processing framework. Based on this framework, any business process can be decomposed and assembled into a Java interface using an annotation + lifecycle approach. You only need to focus on the individual steps of the process, rather than the implementation of the interface.
    Remote has designed a set of callback interfaces that segment each step of Java workflow processing into specific callback interfaces. Users can fully decouple each step of the workflow based on the actual situation of business process handling. Finally, using Remote, these steps are seamlessly integrated to accomplish the desired business logic.
    Currently, Remote primarily serves the construction of client applications for third-party public platform APIs based on the HTTP/HTTPS protocol. With this client, users can call APIs of third-party systems and complete their own business logic processing, similar to writing Feign interfaces.
    Of course, we can still leverage the features brought by remote-core for third-party extensions. For example, using remote-core, you can write code to retrieve data from an Excel spreadsheet, perform calculations based on certain rules, and carry out other business operations.

#### Software Architecture
    remote-core: The core architecture of the Remote framework. This library defines the first-class citizens of Remote: RemoteLifeCycle, LifeCycle, and CallBack. Based on these three interfaces/annotations, users can extend any desired framework. We also hope that participants can unleash their creativity to build a more complete Remote ecosystem.
    remote-net: Remote networking package, defining parts of the library related to networking. By using @Server, users can declare which network system (server) their constructed client wants to connect to. This package also defines the Remote network environment repository interface, which users can use to manage third-party network system environments. It's especially useful for cases where the domain name of third-party public platform APIs is being proxied in an intranet environment.
    remote-net-repository: Currently provides a DB-based Remote network environment repository management solution based on mybatis-plus.
    remote-net-repository-default: Provides the default DB-based network environment database table and its mapping entity.
    remote-net-http: Adapter for Remote API clients under the HTTP/HTTPS protocol. Note: This library is based on netty and provides access functionality to third-party HTTP/HTTPS interfaces under the HTTP/HTTPS protocol.
    remote-spring: Integration of Remote with Spring.
    remote-zen-pom: Provides a version management library similar to spring-boot-dependencies.

#### Installation
```xml
<xml>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.asialjim.microapplet</groupId>
                <artifactId>remote-zen-pom</artifactId>
                <version>${remote.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>io.gitee.asialjim</groupId>
            <artifactId>remote-net-http</artifactId>
        </dependency>
    </dependencies>
</xml>
```

#### Instructions

```java
import java.io.Serializable;

// show me the code
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

#### Contribution

    Fork this repository.
    Create a new branch named Feat_xxx.
    Commit your code changes.
    Create a new Pull Request.
