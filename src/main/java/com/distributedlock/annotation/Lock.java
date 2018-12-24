package com.distributedlock.annotation;

import java.lang.annotation.*;

/**
 * Created by alex on 18/12/24.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Lock {

    String key() default "";//锁的KEY

    String lockPrex() default "LOCK_";//锁的前缀

    int retryCount() default 2;//重试次数

    int maxExistsTime() default 5;//持锁时间 单位：s


}
