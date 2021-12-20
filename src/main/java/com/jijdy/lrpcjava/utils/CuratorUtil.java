package com.jijdy.lrpcjava.utils;

import com.google.common.primitives.Bytes;
import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.integration.ConfigService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* connect and admin Zookeeper with Curator
 * @Author jijdy
 * @Date 2021/12/20 13:08
 */
public class CuratorUtil {
    private static final Logger log = LoggerFactory.getLogger(CuratorUtil.class);

    private static volatile CuratorFramework curator;

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
                }
            }
        }
        return curator;
    }

    public static void registerService(CuratorFramework curator, String serviceName, String data) throws Exception {
        byte[] serviceData = null;
        String path = "/"+serviceName+"/provider";
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
        /* 先删除节点，再将新添加的节点写入，可能需要加个锁 */
        curator.delete().forPath(path);
        curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,bytes);
        log.info("server register service successful: [{}]",serviceName);
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
