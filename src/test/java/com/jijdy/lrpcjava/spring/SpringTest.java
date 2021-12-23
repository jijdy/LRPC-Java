package com.jijdy.lrpcjava.spring;

import com.jijdy.lrpcjava.integration.ConfigService;
import com.jijdy.lrpcjava.integration.postprocessor.ConfigComponent;
import com.jijdy.lrpcjava.spring.bean.MyReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SpringTest {

    @Resource
    MyReference service;

    @Resource
    ConfigComponent configService;

    @Test
    public void test2() {
        int port = ConfigService.getPort();
        System.out.println(configService.getPort());
        System.out.println(configService);
    }


    @Test
    public void invoke() {
        String id = "12324";
        String name = "jijdy";

//         service.invoke();
         while (true) {

         }

    }
}
