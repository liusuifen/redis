package com.example.redisjedis;

import redis.clients.util.JedisClusterCRC16;

/**
 * redis分片算法
 * @author
 *
 */
public class CRC16 {

    public static void main(String[] args){
        String str="name1";
        System.out.println(JedisClusterCRC16.getCRC16(str)%16384);
    }
    
}
