package com.distributedlock.properties;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by alex on 18/12/19.
 */
public class LockProperties {

    private String lockPrex;//锁key的前缀
    private int lockMaxExistTime;// 单位s，加锁操作持有锁的最大时间

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
}
