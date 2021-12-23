package com.jijdy.lrpcjava.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/* configuration for all service
 * @Author jijdy
 * @Date 2021/12/20 13:10
 */
public class ConfigService {

    private static final Boolean empty;

    static {
        empty = true;
    }

    private static String registryAddr;

    private static String namespace;

    private static Integer zkSleepTimeout;

    private static Integer maxRetries;

    private static Integer port;

    private static String serializer;

    private static String loadBalance;

    public static boolean isSet() {
        return empty;
    }

    public static boolean hasPort() {
        return null != port;
    }


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

    public static String getSerializer() {
        if (null == serializer) {
            return "protobuf";
        }
        return serializer;
    }

    public static String getLoadBalance() {
        if (null == loadBalance) {
            return "LRU";
        }
        return loadBalance;
    }

    public static void setRegistryAddr(String registryAddr) {
        ConfigService.registryAddr = registryAddr;
    }

    public static void setNamespace(String namespace) {
        ConfigService.namespace = namespace;
    }

    public static void setZkSleepTimeout(Integer zkSleepTimeout) {
        ConfigService.zkSleepTimeout = zkSleepTimeout;
    }

    public static void setMaxRetries(Integer maxRetries) {
        ConfigService.maxRetries = maxRetries;
    }

    public static void setPort(Integer port) {
        ConfigService.port = port;
    }

    public static void setSerializer(String serializer) {
        ConfigService.serializer = serializer;
    }

    public static void setLoadBalance(String loadBalance) {
        ConfigService.loadBalance = loadBalance;
    }

}
