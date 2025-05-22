# web通用模块

## 1. 登录

### 1.1 使用方式
#### 1.引入pom
```xml
 <dependency>
    <groupId>org.example.server.web</groupId>
    <artifactId>login</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```
#### 2.使用注解
```java
@EnableLogin
// 指定sa-token参数
```

#### 3.实现登录接口
```java
public interface LoginService<T extends BaseLoginUser> {
    /**
     * 登录检查
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    T loginCheck(String username, String password);

    /**
     * 获取登录用户
     * @param loginId 登录ID
     * @return 用户对象
     */
    T getLoginUser(String loginId);

    /**
     * 登录后回调
     * @param loginId 登录ID
     */
    default void afterLogin(String loginId) {}

    /**
     * 注销前回调    
     * @param loginId 登录ID
     */
    default void beforeLogout(String loginId) {}
}

```
#### 4. 实现权限接口
```java
public interface PermissionService {

    /**
     * 请求路由权限
     * @return 权限集合
     */
    default List<RouterPermission> getRouterPermissions() {
        return List.of();
    }

    /**
     * 根据登录ID获取权限码
     * @param loginId 登录ID
     * @return 权限码集合
     */
    List<String> getPermissions(String loginId);

    /**
     * 根据登录ID获取角色码
     * @param loginId 登录ID
     * @return 角色码
     */
    List<String> getRoles(String loginId);
}
```

### 1.2 web登录接口
```
登录  POST /api/user/doLogin {"username": "", "password": ""}
注销  POST /api/user/doLogout
```

## 2. mybatis
```java

// BaseMapper继承 com.baomidou.mybatisplus.core.mapper.BaseMapper，更易用
```

## 3.操作日志
### 3.1 使用方式
1. 引入pom
```xml
 <dependency>
    <groupId>org.example.server.log</groupId>
    <artifactId>operation-log</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

2. 使用注解
```java
// 开启操作日志模块
@EnableOperationLog
```
3. 代码中使用logger
```java
private final OperationLogger opLog = OperationLoggerProxy.createLogger("报表/看板");
public void test() {
    opLog
// 设置log name（可选）
            .name("")
            // .log .error .warn等logger接口函数
            .log();
}
```
4. web接口
```
分页查询接口 /sys/operation-log/page {"filter": {"name":"", "type":"","operatorId": "", "ip": "" }, "pageSize": 10, "pageNum": 1}
```
## 4.用户&角色
### 4.1 使用方式
 1. 引入pom
```xml
 <dependency>
    <groupId>org.example.server.web</groupId>
    <artifactId>user</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```
2. 使用注解
```java
// 开启用户权限模块
@EnableUserModule
```

## 5.web通用
自动包装Result

### 4.1使用方式
1. 引入pom
```xml
 <dependency>
    <groupId>org.example.server.web</groupId>
    <artifactId>web</artifactId>
    <version>1.0.1-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```
2. 使用注解
```java
@EnableWebResult
```

3. 使用新注解
```java
@ResultController("/xxx") 或者 @ResultBody
```