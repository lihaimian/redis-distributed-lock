package com.distributedlock.lock;

import com.distributedlock.properties.LockProperties;

/** 加锁、释放锁操作接口
 * Created by alex on 18/12/18.
 */
public interface ILock {

    /**
     *
     * @param lock
     * @param lockProperties
     */
    /**
     * 获取锁
     * @param lock
     * @param lockProperties
     * @return redisLockKey
     */
    String lock(String lock, LockProperties lockProperties);



    /**
     * 获取锁
     * @param lock
     * @param lockProperties
     * @return redisLockKey
     */
    String unlock(String lock,LockProperties lockProperties);
}
