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
