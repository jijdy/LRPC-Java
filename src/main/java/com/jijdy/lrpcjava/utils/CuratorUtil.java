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
        /* ???????????? */
        getLock(serviceName).acquire();
        try {
            /* ?????????????????????????????????????????????????????????????????? */
            serviceData = getServiceData(curator, serviceName);
        } catch (Exception e) {
            log.info("path:[{}] is empty node!",path);
        }
        byte[] bytes;
        if (null != serviceData) {
            /* ???????????????????????????????????? */
            bytes = Bytes.concat(serviceData, data.getBytes());
        } else {
            bytes = data.getBytes();
        }
        /* todo
           ??????????????????ZooKeeper??????????????????????????????????????????
         */
        Stat stat = curator.checkExists().forPath(path);
        if (null == stat) {
            /* ???????????? */
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,bytes);
        } else {
            /* ????????????????????? */
            curator.setData().forPath(path,bytes);
        }
        /* ????????? */
        getLock(serviceName).release();
        log.info("server register service successful: [{}]",serviceName);
    }

    /* ???????????? */
    public static void unRegisterService(CuratorFramework curator, String serviceName) throws Exception {
        String path = "/"+serviceName+"/provider";
        InterProcessLock lock = getLock(serviceName);
        lock.acquire();
        /* ??????????????????????????? */
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

    public static void close(CuratorFramework curator) {
        curator.close();
    }

}
