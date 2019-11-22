# websocket 消息推送 demo

websocket是一个通信协议，与http、tcp是一个级别的

http协议只能客户端发给服务端，服务端无法推送给客户端，
某些场景很难实现，比如：即时聊天、服务端推送消息给客户端等

这类场景都是服务端推送给客户端，没有websocket前只有通过ajax轮询，即隔几秒发送请求查询后台来实现

本例子演示了如何使用websocket推送到客户端
步骤如下：

1. pom.xml添加依赖
```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
  </dependency>
```
2. 配置类
- 配置一个队列 用户消息交换
- 配置一个stomp端点 用于连接

3. 客户端
使用sockjs和stomp协议与服务端通信
- 连接到服务器
- 订阅队列
- 接收消息

4. 服务端
推送消息使用 SimpMessagingTemplate

