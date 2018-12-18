package com.distributedlock.manager;

import com.distributedlock.lock.ILock;
import com.distributedlock.lock.LuaDistributedLock;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/** 加分布工锁并执行业务实现类
 * Created by alex on 18/12/19.
 */
@Component
public class LuaRedisLockManager implements ILockManager {

    @Autowired
    private ILock distributeLock;

    public void callBack(String lockKey, LockCallBack callBack) {
        Assert.notNull(lockKey,"lockKey can't not be null");
        Assert.notNull(callBack,"callBack can't not be null");
        try{
            //获取锁
            distributeLock.lock(lockKey);
            //执行业务
            callBack.execute();
        }finally{
            //释放锁
            distributeLock.unlock(lockKey);
        }
    }

    public <T> T callBack(String lockKey, ReturnCallBack<T> callBack) {
        Assert.notNull(lockKey,"lockKey can't not be null");
        Assert.notNull(callBack,"callBack can't not be null");
        try{
            //获取锁
            distributeLock.lock(lockKey);
            //执行业务
            return callBack.execute();
        }finally{
            //释放锁
            distributeLock.unlock(lockKey);
        }
    }


}
