package com.distributedlock.aspect;

import com.distributedlock.annotation.Lock;
import com.distributedlock.annotation.LockKey;
import com.distributedlock.lock.ILock;
import com.distributedlock.properties.LockProperties;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/** 针对加了@Lock的函数进行分布式锁的增强处理
 * Created by alex on 18/12/24.
 */
@Component
@Aspect
public class DistributedLockAspect {

    private Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);

    @Autowired
    private ILock distributeLock;
    @Autowired
    private LockProperties lockProperties;

    @Pointcut("@annotation(com.distributedlock.annotation.Lock)")
    public void advice(){
    }

    /**
     * 对使用了Lock的注解进行解析加入分布式锁
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("advice()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        LockProperties lockProp = new LockProperties();
        BeanUtils.copyProperties(lockProperties,lockProp);
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Lock lock = method.getAnnotation(Lock.class);
        if(StringUtils.isNotBlank(lock.lockPre())){
            lockProp.setLockPre(lock.lockPre());
        }
        if(lock.retryCount()>0){
            lockProp.setRetryCount(lock.retryCount());
        }
        if(lock.expiredTime()>0){
            lockProp.setExpiredTime(lock.expiredTime());
        }
        String key = lock.key();
        //若存在@LockKey的入参，则将其设置为分布式锁的KEY
        String paramKey = paramToKey(method,pjp.getArgs());
        if(StringUtils.isNotBlank(paramKey)){
            key = key + paramKey;
        }
        //分布式锁没有key，则取其类名+方法名
        if (StringUtils.isEmpty(key)) {
            String className = pjp.getTarget().getClass().getName();
            String methodName = method.getName();
            key = className + ":" + methodName;
        }
        try{
            String lockKey = distributeLock.lock(key,lockProp);
            Object object =  pjp.proceed();
            logger.debug("加锁业务执行成功，lockKey:{}，准备释放锁",lockKey);
            return object;
        }finally {
            distributeLock.unlock(key,lockProp);
        }
    }

    /**
     * 根据函数的入参及参数注解，将存在注解的参数拼接成字符串作为KEY
     * @param method
     * @param args
     * @return
     */
    private String paramToKey(Method method,Object[] args){
        StringBuilder sb = new StringBuilder();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for(int i=0;i<paramAnnotations.length;i++){
            Annotation[] annotation = paramAnnotations[i];
            if(annotation.length>0){
                for(int j=0;j<annotation.length;j++){
                    if(annotation[j] instanceof LockKey){
                        if(sb.length()>0){
                            sb.append(":");
                        }
                        sb.append(args[i]);
                    }
                }
            }
        }
        return sb.toString();
    }

}
