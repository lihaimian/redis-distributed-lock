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

    String lockPre() default "";//锁的前缀

    int retryCount() default 0;//重试次数

    int expiredTime() default 0;//持锁时间 单位：s


}
