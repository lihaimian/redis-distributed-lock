package com.distributedlock.manager;

/** 有返回值的业务执行接口
 * Created by alex on 18/12/19.
 */
public interface ReturnCallBack<T> {
    T execute();
}
