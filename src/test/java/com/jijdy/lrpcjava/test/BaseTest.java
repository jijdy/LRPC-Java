package com.jijdy.lrpcjava.test;

import com.google.common.primitives.Bytes;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class BaseTest {

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
}
