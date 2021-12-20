package com.jijdy.lrpcjava.loadbalance;

/* --
 * @Author jijdy
 * @Date 2021/12/20 15:09
 */
public abstract class LoadBalance {

    /* 下标从1开始 */
    public String findService(byte[] data) {
        String s = new String(data);
        String[] split = s.split("/");
        if (split.length == 2) {
            return split[1];
        }
        return this.route(split);
    }



    /* 负载均衡得到需要被连接的地址 */
    public abstract String route(String[] routes);
}
