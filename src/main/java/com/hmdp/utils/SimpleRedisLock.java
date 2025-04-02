package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

public class SimpleRedisLock implements ILock{

    private String name;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX = "lock:";

    @Override
    public boolean tryLock(long timeoutSec) {
        return false;
    }

    @Override
    public void unlock() {

    }
}
