package com.jijdy.lprctest;

import com.jijdy.lprctest.spring.bean.MyReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LprcTestApplicationTests {

    @Resource
    MyReference reference;

    @Test
    void contextLoads() {
        reference.invoke();
    }

}
