package com.distributedlock.lock;


import com.distributedlock.properties.LockProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** redis分布式锁加锁、释放锁实现
 * Created by alex on 18/12/18.
 */
@Component
//@EnableConfigurationProperties(LockProperties.class)
public class LuaDistributedLock implements ILock,InitializingBean{

    private static final int LOCK_MAX_EXIST_TIME = 5;  // 单位s，加锁操作持有锁的最大时间
    private static final String LOCK_PREX = "lock_"; // 锁的key的前缀

    @Autowired
    private LockProperties lockProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private DefaultRedisScript<Long> lockScript; // 锁脚本
    private DefaultRedisScript<Long> unlockScript; // 解锁脚本

    private ThreadLocal<String> threadKeyId = new ThreadLocal<String>(){
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString().replace("-","");
        }
    };

    /**
     * 生成操作redis的lua脚本操作实例
     */
    private void initialize(){
        //lock script
        lockScript = new DefaultRedisScript<Long>();
        lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock/lock.lua")));
        lockScript.setResultType(Long.class);

        //unlock script
        unlockScript = new DefaultRedisScript<Long>();
        unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock/unlock.lua")));
        unlockScript.setResultType(Long.class);

    }

    public void lock(String lock) {
        Assert.notNull(lock,"lock can't be null");
        String key = getLockKey(lock);
        List<String> keyList = new ArrayList<String>();
        keyList.add(key);
        keyList.add(threadKeyId.get());
        while(true){
            if(redisTemplate.execute(lockScript,keyList,String.valueOf(lockProperties.getLockMaxExistTime() * 1000))>0){
                //返回结果大于0，表示加锁成功
                break;
            }else{
                try{
                    Thread.sleep(10,(int)(Math.random() * 500));
                }catch(InterruptedException e){
                    break;
                }
            }
        }

    }

    public void unlock(String lock) {
        final String lockKey = getLockKey(lock);
        List<String> keyList = new ArrayList<String>();
        keyList.add(lockKey);
        keyList.add(threadKeyId.get());
        redisTemplate.execute(unlockScript, keyList);
    }

    /**
     * 生成key
     * @param lock
     * @return
     */
    private String getLockKey(String lock){
        StringBuilder sb = new StringBuilder();
        sb.append(lockProperties.getLockPrex()).append(lock);
        return sb.toString();
    }

    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isEmpty(lockProperties.getLockPrex())){
            lockProperties.setLockPrex(LOCK_PREX);
        }
        if(lockProperties.getLockMaxExistTime()<=0){
            lockProperties.setLockMaxExistTime(LOCK_MAX_EXIST_TIME);
        }
        initialize();
    }
}
