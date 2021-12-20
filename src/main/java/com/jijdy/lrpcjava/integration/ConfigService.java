package com.jijdy.lrpcjava.integration;

/* configuration for all service
 * @Author jijdy
 * @Date 2021/12/20 13:10
 */
public class ConfigService {

    private static String registryAddr;

    private static String namespace;

    private static Integer zkSleepTimeout;

    private static Integer maxRetries;

    private static Integer port;

    public static int getPort() {
        if (null == port) {
            return 13578;
        }
        return port;
    }

    public static String getNamespace() {
        if (null == namespace) {
            return "l-rpc";
        }
        return namespace;
    }

    public static String getRegistryAddr() {
        if (null == registryAddr) {
            return "127.0.0.1:2181";
        }
        return registryAddr;
    }

    public static int getZkSleepTimeout() {
        if (null == zkSleepTimeout) {
            return 1000;
        }
        return zkSleepTimeout;
    }

    public static int getMaxRetries() {
        if (null == maxRetries) {
            return 10;
        }
        return maxRetries;
    }
}
