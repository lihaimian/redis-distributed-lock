package com.distributedlock.lock;


import com.distributedlock.manager.LuaRedisLockManager;
import com.distributedlock.properties.LockProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * redis分布式锁加锁、释放锁实现
 * Created by alex on 18/12/18.
 */
@Component
public class LuaDistributedLock implements ILock, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(LuaRedisLockManager.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    private DefaultRedisScript<Long> lockScript; // 锁脚本
    private DefaultRedisScript<Long> unlockScript; // 解锁脚本

    private ThreadLocal<String> threadKeyId = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    };

    /**
     * 生成操作redis的lua脚本操作实例
     */
    private void initialize() {
        //lock script
        lockScript = new DefaultRedisScript<Long>();
        lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock/lock.lua")));
        lockScript.setResultType(Long.class);
        logger.debug("初始化加锁脚本成功:\n:{}",lockScript.getScriptAsString());

        //unlock script
        unlockScript = new DefaultRedisScript<Long>();
        unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock/unlock.lua")));
        unlockScript.setResultType(Long.class);
        logger.debug("初始化释放锁脚本成功:\n:{}",unlockScript.getScriptAsString());
    }

    public String lock(String lock,LockProperties lockProperties) {
        Assert.notNull(lock, "lock can't be null");
        String key = getLockKey(lock,lockProperties);
        List<String> keyList = new ArrayList<String>();
        keyList.add(key);
        keyList.add(threadKeyId.get());
        int lockTryCount = 0;//获取锁次数
        logger.debug("加锁成功，lockKey:{},准备执行业务操作",key);
        while (true) {
            if (lockProperties.getRetryCount() > 0 && lockTryCount > lockProperties.getRetryCount()) {
                //加锁失败超过设定重试次数，抛出异常表示加锁失败
                throw new RuntimeException("access to distributed lock more than retries："+lockProperties.getRetryCount());
            }
            if(lockTryCount>0){
                logger.debug("重试获取锁:{}操作:{}次",key,lockTryCount);
            }
            if (redisTemplate.execute(lockScript, keyList, String.valueOf(lockProperties.getExpiredTime() * 1000)) > 0) {
                //返回结果大于0，表示加锁成功
                logger.debug("加锁成功，lockKey:{},准备执行业务操作",key);
                return key;
            } else {
                try {
                    Thread.sleep(10, (int) (Math.random() * 500));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lockTryCount++;
        }
    }

    public String unlock(String lock,LockProperties lockProperties) {
        final String lockKey = getLockKey(lock,lockProperties);
        List<String> keyList = new ArrayList<String>();
        keyList.add(lockKey);
        keyList.add(threadKeyId.get());
        redisTemplate.execute(unlockScript, keyList);
        logger.debug("成功释放锁，lockKey:{}",lockKey);
        return lockKey;
    }

    /**
     * 生成key
     *
     * @param lock
     * @return
     */
    private String getLockKey(String lock,LockProperties lockProperties) {
        if(StringUtils.isBlank(lockProperties.getLockPre())){
            return lock;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(lockProperties.getLockPre()).append(lock);
        return sb.toString();
    }

    public void afterPropertiesSet() throws Exception {
        initialize();
    }
}
