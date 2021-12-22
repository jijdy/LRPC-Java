package com.jijdy.lrpcjava.loadbalance;

/* --
 * @Author jijdy
 * @Date 2021/12/20 15:09
 */
public abstract class LoadBalance {

    /* 下标从1开始，若只有一个提供者，则不做负载均衡，直接返回地址 */
    public String findService(String[] split) {
        if (split.length == 1) {
            return split[0];
        }
        return this.route(split);
    }


    /* 负载均衡得到需要被连接的地址 */
    public abstract String route(String[] routes);
}
