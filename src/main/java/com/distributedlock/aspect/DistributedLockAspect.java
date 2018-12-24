package com.distributedlock.aspect;

import com.distributedlock.annotation.Lock;
import com.distributedlock.annotation.LockKey;
import com.distributedlock.lock.ILock;
import com.distributedlock.manager.ILockManager;
import com.distributedlock.manager.LockCallBack;
import com.distributedlock.properties.LockProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by alex on 18/12/24.
 */
@Component
@Aspect
public class DistributedLockAspect {

    @Autowired
    private ILock distributeLock;
    @Autowired
    private LockProperties lockProperties;

    @Pointcut("@annotation(com.distributedlock.annotation.Lock)")
    public void advice(){
    }

    @Around("advice()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        LockProperties lockProp = new LockProperties();
        BeanUtils.copyProperties(lockProperties,lockProp);
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Lock lock = method.getAnnotation(Lock.class);
        if(StringUtils.isEmpty(lock.lockPrex())){
            lockProp.setLockPrex(lock.lockPrex());
        }
        if(lock.retryCount()>0){
            lockProp.setRetryCount(lock.retryCount());
        }
        if(lock.maxExistsTime()>0){
            lockProp.setLockMaxExistTime(lock.maxExistsTime());
        }
        String key = lock.key();
        //若存在参数作为key，则设置参数作为key
        String paramKey = paramToKey(method,pjp.getArgs());
        if(!StringUtils.isEmpty(paramKey)){
            key = key + paramKey;
        }
        //分布式锁没有key，则取其类名+方法名
        if (StringUtils.isEmpty(key)) {
            String className = pjp.getTarget().getClass().getName();
            String methodName = method.getName();
            key = className + ":" + methodName;
        }
        try{
            distributeLock.lock(key,lockProp);
            return pjp.proceed();
        }finally {
            distributeLock.unlock(key,lockProp);
        }
    }

    private String paramToKey(Method method,Object[] args){
//        List<Object> objs = new ArrayList<Object>();
//        Annotation[][] paramAnnotations = method.getParameterAnnotations();
//        for(int i=0;i<paramAnnotations.length;i++){
//            Annotation[] annotations = paramAnnotations[i];
//            if (annotations.length > 0) {
//                Optional<Annotation> optional = Arrays.stream(annotations).filter(p -> p instanceof LockKey).findFirst();
//                if (optional != null) {
//                    LockKey lockKey = LockKey.class.cast(optional.get());
//                    objs.addAll(parseAnnotation(lockKey, args[i]));
//                }
//            }
//        }
        return null;

    }

}
