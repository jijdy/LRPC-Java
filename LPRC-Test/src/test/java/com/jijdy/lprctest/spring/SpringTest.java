package com.jijdy.lprctest.spring;

import com.jijdy.lprctest.spring.bean.MyReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@SpringBootTest
public class SpringTest {

    @Resource
    MyReference service;

    @Test
    public void invoke() {
        String id = "12324";
        String name = "jijdy";

         service.invoke();

    }
}
