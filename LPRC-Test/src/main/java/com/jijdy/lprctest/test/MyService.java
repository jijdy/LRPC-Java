package com.jijdy.lprctest.test;

import com.jijdy.lrpcjava.annotation.LRPCService;
import com.jijdy.lrpcjava.test.Service;
import org.springframework.stereotype.Component;

@Component
@LRPCService(version = "1.0")
public class MyService implements Service {

    @Override
    public String getService(String id,String name) {
        return "MyService: "+id+"---"+name;
    }

}
