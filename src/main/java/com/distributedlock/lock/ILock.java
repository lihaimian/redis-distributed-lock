package com.distributedlock.lock;

import com.distributedlock.properties.LockProperties;

/** 加锁、释放锁操作接口
 * Created by alex on 18/12/18.
 */
public interface ILock {

    /**
     * 获取锁
     * @param lock
     * @param lockProperties
     */
    void lock(String lock, LockProperties lockProperties);


    /**
     * 释放锁
     * @param lock
     * @param lockProperties
     */
    void unlock(String lock,LockProperties lockProperties);
}
