package com.distributedlock;

import com.distributedlock.properties.LockProperties;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by alex on 18/12/19.
 */
@Configurable
@ComponentScan
public class AutoConfiguration {

//    @Bean
//    @ConfigurationProperties(prefix = "redis.distributed.lock")
//    public LockProperties lockProperties(){
//        return new LockProperties();
//    }
}
