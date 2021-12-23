package com.jijdy.lrpcjava.spring.bean;

import com.jijdy.lrpcjava.annotation.LRPCReference;
import com.jijdy.lrpcjava.test.Service;
import org.springframework.stereotype.Component;

@Component
public class MyReference {

    @LRPCReference(version = "1.0")
    Service service;

    public void invoke() {
        String id = "12324";
        String name = "jijdy";

        String service = this.service.getService(id, name);
        System.out.println("得到从远端调用得到的数据"+service);
    }
}
