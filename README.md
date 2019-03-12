# redis-distributed-lock

#### 介绍
集成spring boot框架的redis分布式锁，使用redis作为锁的资源存储，使用lua脚本加锁保证锁的原子性操作

#### 软件架构
软件架构说明


#### 安装教程
```
1. 安装
    将工程下载到本地，导入IDE开发工具，进行install。
2. 添加依赖
    在需要使用该分布式锁工程的pom.xml文件中加入以下依赖：
    <dependency>
        <groupId>com.springboot.redis</groupId>
    	<artifactId>redis-distributed-lock</artifactId>
        <version>1.0.0</version>
    </dependency>

```


#### 使用说明

```
全局统一配置：
1.redis配置
     在Spring的配置文件bootstrap.properties或application.properties中添加redis配置信息

    #redis配置
    spring.redis.host=127.0.0.1
    spring.redis.port=6379
    spring.redis.database=0
    
    #可选全局配置
    #分布式锁的KEY前缀
    redis.distributed.lock.lock-pre=MY_LOCK
    #分布式锁的持锁有效时长(单位：s)
    redis.distributed.lock.expired-time=30
    #分布式锁的获取锁失败重试次数
    redis.distributed.lock.retry-count=2

    ![输入图片说明](https://images.gitee.com/uploads/images/2019/0310/221937_2798ace7_1993405.png "全局配置信息")

```

方式一：注入方式

```
1.在需要使用锁的实例Bean中添加分布式锁的操作Bean和全局配置信息
    @Autowired
    private ILockManager lockManager;
    @Autowired
    private LockProperties lockProperties;
2.在需要加锁的函数中使用
	/**
     * 无返回值分布式锁注入操作
     * @param userName
     */
    @Override
    public void sayHello(String userName) {
        String lockKey = UUID.randomUUID().toString();
        //将函数的业务操作在加锁操作中完成
        lockManager.callBack(lockKey, new LockCallBack() {
            @Override
            public void execute() {
                System.out.println("Hello,I'm alex.");
            }
        });
    }
    /**
     * 有返回值分布式锁注入操作
     * @param id
     * @param userName
     * @return
     */
    @Override
    public String sayHello(String id, String userName) {
        String lockKey = UUID.randomUUID().toString();
        //将函数的业务操作在加锁操作中完成
        return (String) lockManager.callBack(lockKey, new ReturnCallBack<Object>() {
            @Override
            public Object execute() {
                return "Hello,I'm alex.this is return message.";
            }
        });
    }

![输入图片说明](https://images.gitee.com/uploads/images/2019/0310/222015_e14b4b80_1993405.png "使用方式一")
    
```

方式二：注解方式



```
	1.直接在需要加锁的业务函数中添加@Lock注解即可。
	/**
     * 无返回值分布工锁注解操作
     * @param userName
     */
    @Lock
    @Override
    public void loadUser(String userName) {
        System.out.println("Hello,I'm alex.use by annotation");
    }
    /**
     * 有返回值分布式锁注解操作
     * @param id
     * @param userName
     * @param age
     * @return
     */
    @Lock
    @Override
    public String loadUser(@LockKey String id, String userName, @LockKey Integer age) {
        return "Hello,I'm alex.this is return message.use by annotation";
    }
    分布式锁KEY说明
    使用注解方式的分布式锁KEY
    1.默认情况下，会使用类名+函数名作为分布式锁的KEY。
    2.函数的入参可以添加@LockKey注解，分布式锁将使用函数中所有添加了@LockKey的参数值拼接作为分布式锁的KEY

    ![输入图片说明](https://images.gitee.com/uploads/images/2019/0310/222053_513b720b_1993405.png "使用方式二")

```
