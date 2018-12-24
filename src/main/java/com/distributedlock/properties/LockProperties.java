package com.distributedlock.properties;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by alex on 18/12/19.
 */
@Configuration
@ConfigurationProperties(prefix = "redis.distributed.lock")
public class LockProperties {

    private String lockPrex;//锁key的前缀
    private int lockMaxExistTime = 0;// 单位s，加锁操作持有锁的最大时间
    private int retryCount = 0;//重试次数

    public int getLockMaxExistTime() {
        return lockMaxExistTime;
    }

    public void setLockMaxExistTime(int lockMaxExistTime) {
        this.lockMaxExistTime = lockMaxExistTime;
    }

    public String getLockPrex() {
        return lockPrex;
    }

    public void setLockPrex(String lockPrex) {
        this.lockPrex = lockPrex;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
