package com.distributedlock.manager;

import com.distributedlock.lock.ILock;
import com.distributedlock.lock.LuaDistributedLock;
import com.distributedlock.properties.LockProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/** 加分布工锁并执行业务实现类
 * Created by alex on 18/12/19.
 */
@Component
public class LuaRedisLockManager implements ILockManager,InitializingBean {

    private static final int LOCK_MAX_EXIST_TIME = 5;  // 单位s，加锁操作持有锁的最大时间
    private static final String LOCK_PREX = "LOCK_"; // 锁的key的前缀

    @Autowired
    private ILock distributeLock;
    @Autowired
    private LockProperties lockProperties;

    public void callBack(String lockKey, LockCallBack callBack) {
        Assert.notNull(lockKey,"lockKey can't not be null");
        Assert.notNull(callBack,"callBack can't not be null");
        try{
            //获取锁
            distributeLock.lock(lockKey,lockProperties);
            //执行业务
            callBack.execute();
        }finally{
            //释放锁
            distributeLock.unlock(lockKey,lockProperties);
        }
    }

    public <T> T callBack(String lockKey, ReturnCallBack<T> callBack) {
        Assert.notNull(lockKey,"lockKey can't not be null");
        Assert.notNull(callBack,"callBack can't not be null");
        try{
            //获取锁
            distributeLock.lock(lockKey,lockProperties);
            //执行业务
            return callBack.execute();
        }finally{
            //释放锁
            distributeLock.unlock(lockKey,lockProperties);
        }
    }


    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.isEmpty(lockProperties.getLockPrex())) {
            lockProperties.setLockPrex(LOCK_PREX);
        }
        if (lockProperties.getLockMaxExistTime() > 0) {
            lockProperties.setLockMaxExistTime(LOCK_MAX_EXIST_TIME);
        }
    }
}
