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
