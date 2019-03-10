package com.distributedlock;


import com.distributedlock.annotation.Lock;
import com.distributedlock.annotation.LockKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLock {


    @Test
    public void testLock(){
        String msg = sayName(UUID.randomUUID().toString(),"alex");
        System.out.println(msg);
    }



    @Lock
    public String sayName(@LockKey String id, String name){
        return "hello,my name is: " + name;
    }

}
