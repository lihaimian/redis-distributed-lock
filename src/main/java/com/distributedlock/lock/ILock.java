package com.distributedlock.lock;

/** 加锁、释放锁操作接口
 * Created by alex on 18/12/18.
 */
public interface ILock {

    /**
     * 获取锁
     * @param lock 锁Key
     */
    void lock(String lock);

    /**
     * 释放锁
     * @param lock 锁Key
     */
    void unlock(String lock);
}
