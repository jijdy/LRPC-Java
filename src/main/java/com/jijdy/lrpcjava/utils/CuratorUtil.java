package com.jijdy.lrpcjava.utils;

import com.google.common.primitives.Bytes;
import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.integration.ConfigService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* connect and admin Zookeeper with Curator
 * @Author jijdy
 * @Date 2021/12/20 13:08
 */
public class CuratorUtil {
    private static final Logger log = LoggerFactory.getLogger(CuratorUtil.class);

    private static volatile CuratorFramework curator;

    private static Map<String,InterProcessLock> lockMap;

    private static String addr;

    static {
        try {
            addr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(),ConfigService.getPort()).toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static CuratorFramework getCuratorFramework() {
        if (null == curator) {
            synchronized (CuratorUtil.class) {
                if (null == curator) {
                    curator = CuratorFrameworkFactory.builder()
                            .connectString(ConfigService.getRegistryAddr())
                            .namespace(ConfigService.getNamespace())
                            .retryPolicy(new ExponentialBackoffRetry(ConfigService.getZkSleepTimeout(),ConfigService.getMaxRetries()))
                            .build();
                    curator.start();
                    lockMap = new ConcurrentHashMap<>();
                }
            }
        }
        return curator;
    }

    protected static InterProcessLock getLock(String serviceName) {
        if (lockMap.containsKey(serviceName)) {
            return lockMap.get(serviceName);
        } else {
            String lockPath = "/config/lock/"+serviceName;
            InterProcessMutex interProcessMutex = new InterProcessReadWriteLock(curator, lockPath).writeLock();
            lockMap.put(serviceName,interProcessMutex);
            return interProcessMutex;
        }
    }

    public static void registerService(CuratorFramework curator, String serviceName, String data) throws Exception {
        byte[] serviceData = null;
        String path = "/"+serviceName+"/provider";
//        String lockPath =
        /* 加个写锁 */
        getLock(serviceName).acquire();
        try {
            /* 会先访问得到所有的数据，再将其数据做一个拼接 */
            serviceData = getServiceData(curator, serviceName);
        } catch (Exception e) {
            log.info("path:[{}] is empty node!",path);
        }
        byte[] bytes;
        if (null != serviceData) {
            /* 进行服务提供方的地址拼接 */
            bytes = Bytes.concat(serviceData, data.getBytes());
        } else {
            bytes = data.getBytes();
        }
        /* todo
           考虑是否使用ZooKeeper的监听机制，来实时的更新数据
         */
        Stat stat = curator.checkExists().forPath(path);
        if (null == stat) {
            /* 创建节点 */
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,bytes);
        } else {
            /* 更新节点的数据 */
            curator.setData().forPath(path,bytes);
        }
        /* 释放锁 */
        getLock(serviceName).release();
        log.info("server register service successful: [{}]",serviceName);
    }

    /* 服务注销 */
    public static void unRegisterService(CuratorFramework curator, String serviceName) throws Exception {
        String path = "/"+serviceName+"/provider";
        InterProcessLock lock = getLock(serviceName);
        lock.acquire();
        /* 得到更新之后的数据 */
        byte[] bytes = curator.getData().forPath(path);
        String address = addr;
        String s = new String(bytes);
        int beginIndex = s.indexOf(address);
        bytes = (s.substring(0,beginIndex)+s.substring(beginIndex+address.length())).getBytes();

        curator.setData().forPath(path,bytes);
        lock.release();
        log.info("service [{}] address [{}] unregister!",serviceName,address);
    }

    public static byte[] getServiceData(CuratorFramework curator,String serviceName) throws Exception {
        String path = "/"+serviceName+"/provider";
        byte[] bytes = curator.getData().forPath(path);
        if (null == bytes) {
            throw new RPCException(RPCErrorEnum.SERVICE_NOT_REGISTER,"serviceName:"+serviceName);
        }
        return bytes;
    }
}
