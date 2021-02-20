# dubbo介绍
优秀的 RPC 服务治理框架，直接查看 [官网]( http://dubbo.apache.org/zh/).

# nacos 介绍
作为 注册中心 和 参数配置中心使用， 本次作为dubbo 服务注册发现中心，介绍查看[官网]( https://nacos.io/zh/docs/what-is-nacos.html).

# spring boot 注解方式配置
本次测试环境
springboot 2.2.6
dubbo 2.7.7

==通过反复查看官网和官方demo， 其实基本可以不用 spring-boot-starter-dubbo 配置dubbo，dubbo本身就支持注解方式配置。==

## maven 整体工程目录如下
工程本身即是服务提供者也是服务调用者
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200528145812313.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2d6dDE5ODgxMTIz,size_16,color_FFFFFF,t_70)

配置的 pom文件如下
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.6.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.middol</groupId>
    <artifactId>dubbo-test1</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <java.version>1.8</java.version>
        <dubbo.version>2.7.7</dubbo.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>${dubbo.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>${dubbo.version}</version>
        </dependency>
    </dependencies>
</project>

```
application.yml 配置如下
```yaml
server:
  port: 8081

# log详细配置请查看 logback-spring.xml, springboot可自动加载 resources/logback-spring.xml
logging:
  level:
    root: INFO

#spring:
#  main:
#    allow-bean-definition-overriding: true

dubbo:
  application:
    name: dubbo-samples-annotation

  # 注册中心配置。对应的配置类： org.apache.dubbo.config.RegistryConfig。同时如果有多个不同的注册中心，
  # 可以声明多个 <dubbo:registry> 标签，并在 <dubbo:service> 或 <dubbo:reference> 的 registry 属性指定使用的注册中心。
  registry:
    address: nacos://你的nacos服务器ip:8848
    check: false

  # 服务提供者协议配置。对应的配置类： org.apache.dubbo.config.ProtocolConfig。
  # 同时，如果需要支持多协议，可以声明多个 <dubbo:protocol> 标签，并在 <dubbo:service> 中通过 protocol 属性指定使用的协议。
  protocol:
    name: dubbo
    port: 20880
  # 服务消费者缺省值配置。配置类： org.apache.dubbo.config.ConsumerConfig 。
  # 同时该标签为 <dubbo:reference> 标签的缺省值设置

  consumer:
    timeout: 30000
    retries: 0
    check: false

```
主要注意上面的 retries  check 参数，
check为false 表示 springboot 启动暂不查验服务是否存在或服务是否可注册成功， 
如果为 true 很可能导致启动失败，例如注册中心不存在，服务提供者不存在时。
check 默认为true

具体的其他参数查看 官方配置文档
http://dubbo.apache.org/zh-cn/docs/user/references/xml/dubbo-service.html

## 接口 api
很简单就两测试方法
```java
package com.middol.dubbo.api;

/**
 * @author admin
 */
public interface HelloService {

    /**
     * sayHello
     *
     * @param name ignore
     * @return String
     */
    String sayHello(String name);

    /**
     * sayGoodbye
     *
     * @param name aa
     * @return string
     */
    default String sayGoodbye(String name) {
        return "Goodbye, " + name;
    }
}

```
## 服务提供者
```java
package com.middol.dubbo.impl.provider;


import com.middol.dubbo.api.HelloService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 服务提供者
 *
 * @author admin
 */
@DubboService(version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        System.out.println("provider received invoke of sayHello: " + name);
        sleepWhile();
        return "Annotation, hello " + name;
    }

    @Override
    public String sayGoodbye(String name) {
        System.out.println("provider received invoke of sayGoodbye: " + name);
        sleepWhile();
        return "Goodbye, " + name;
    }

    private void sleepWhile() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

```
==这里注意 如果version 赋值了，请在消费端配置时保持version一致哦==


## 服务消费者
实际情况是， 服务提供者将 api接口 打成jar包，放入maven私服中，服务消费端下载该jar包，本次就省略了该步骤， 消费者服务者在一个工程里面。
```java
package com.middol.dubbo.consumer;

import com.middol.dubbo.api.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * 服务订阅者
 *
 * @author admin
 */
@Component
public class TestConsumerService {

    @DubboReference(version = "1.0",consumer = "hello测试")
    private HelloService helloService;


    /**
     * sayHello
     *
     * @param name ignore
     * @return String
     */
    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
    
    /**
     * sayGoodbye
     *
     * @param name aa
     * @return string
     */
    public String sayGoodbye(String name) {
        return helloService.sayGoodbye(name);
    }
}

```
主要是 注解   @DubboReference(version = "1.0",consumer = "hello测试") 表示引入服务提供者的api接口


## dubbo扫描注解配置
```java
package com.middol.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统入口
 *
 * @author <a href="mailto:guzhongtao@middol.com">guzhongtao</a>
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = {"com.middol.dubbo.impl.provider", "com.middol.dubbo.consumer"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```
==上面的 @EnableDubbo 就是配置 消费者的包路径  提供者的包路径==



## 访问测试
先启动 nacos ，然后springboot， 不出意外会抛出异常
```
APPLICATION FAILED TO START

Description:

The bean 'dubboBootstrapApplicationListener' could not be registered. A bean with that name has already been defined and overriding is disabled.

Action:
```
这里查看 github  dubbo Issues  [6231]( https://github.com/apache/dubbo/issues/6231).  
可明白原因， 当然临时解决可以在application.yml里面加入以下信息：
```yaml
spring:
  main:
    allow-bean-definition-overriding: true
```

写个controller 类测试一下吧

```java

package com.middol.dubbo.controller;

import com.middol.dubbo.consumer.TestConsumerService;
import com.middol.dubbo.impl.provider.HelloServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * controller 测试入口
 *
 * @author admin
 */
@RestController
@RequestMapping("/")
public class TestController {

    @Resource
    private HelloServiceImpl annotationHelloService;

    @Resource
    private TestConsumerService testConsumerService;

    @GetMapping("localtest")
    public String localtest() {
        annotationHelloService.sayHello("我是本地方法 sayHello");
        annotationHelloService.sayGoodbye("我是本地方法 sayGoodbye");
        return "localtest ok";
    }

    @GetMapping("dubbotest")
    public String dubbotest() {
        testConsumerService.sayHello("我是dubbo方法 sayHello");
        testConsumerService.sayGoodbye("我是dubbo方法 sayGoodbye");
        return "dubbotest ok";
    }
}

```

理论上可以调用成功，本人测试通过， 查看nacos 管理平台可发现服务注册情况。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200528151701293.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2d6dDE5ODgxMTIz,size_16,color_FFFFFF,t_70)
