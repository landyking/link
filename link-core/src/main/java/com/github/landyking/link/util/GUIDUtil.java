package com.github.landyking.link.util;

import xyz.downgoon.snowflake.Snowflake;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * @author: Landy
 * @date: 2017/11/6 19:33
 * note:
 */
public class GUIDUtil {
    /*private final static Sequence sequence = new Sequence();

    public static Long nextId() {
        return sequence.nextId();
    }
    */
    private final static Snowflake snowflake=initSnowflake();

    private static Snowflake initSnowflake() {
        long datacenterId=getDatacenterId(31);
        long workerId=getMaxWorkerId(datacenterId,31);
        return new Snowflake(datacenterId,workerId);
    }

    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            System.err.println(" getDatacenterId: " + e.getMessage());
        }
        return id;
    }
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name != null && "".equals(name)) {
            // GET jvmPid
            mpid.append(name.split("@")[0]);
        }
        //MAC + PID 的 hashcode 获取16个低位
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }
    public static Long nextId() {
        return snowflake.nextId();
    }
}
