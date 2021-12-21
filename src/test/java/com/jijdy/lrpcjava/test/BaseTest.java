package com.jijdy.lrpcjava.test;

import com.google.common.primitives.Bytes;
import com.jijdy.lrpcjava.utils.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class BaseTest {

    @Test
    public void lockTest() throws Exception {
        CuratorFramework curator = CuratorUtil.getCuratorFramework();
        String lockPath = "/config/lock/"+"serviceName";
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curator, lockPath);
        InterProcessMutex interProcessMutex = interProcessReadWriteLock.writeLock();
        interProcessMutex.acquire();
        Thread.sleep(10000);
        interProcessMutex.release();
    }

    @Test
    public void test1() throws UnknownHostException {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(),111);
        String x = address.toString();
        System.out.println(x);
        byte[] bytes = x.getBytes();
        byte[] b = new byte[bytes.length];
        b = bytes;
        byte[] concat = Bytes.concat(b, bytes);
        String x11 = new String(concat);
        System.out.println(x11);
        String[] strings = x11.split("/");
        System.out.println(Arrays.toString(strings));
        int i = strings[1].indexOf(":");
        String substring = strings[1].substring(i + 1);
        System.out.println(strings[2]);
        System.out.println(strings[1].substring(0,i));
        System.out.println(substring);
        String x1 = new String(bytes);
        assert x1.equals(x);
    }

    @Test
    public void test2() {
        String s = "/234324/234315";
        String s1 = "/23431";
        int i = s.indexOf(s1);
        s = s.substring(0,i)+s.substring(i+s1.length());
        System.out.println(s);
    }
}
