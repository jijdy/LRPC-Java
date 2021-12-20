package com.jijdy.lrpcjava.loadbalance.impl;

import com.jijdy.lrpcjava.loadbalance.LoadBalance;

import java.util.Random;

/* --
 * @Author jijdy
 * @Date 2021/12/20 15:43
 */
public class RandomLoadBalance extends LoadBalance {

    @Override
    public String route(String[] routes) {
        Random random = new Random();
        int i = random.nextInt(routes.length);
        return routes[i];
    }
}
