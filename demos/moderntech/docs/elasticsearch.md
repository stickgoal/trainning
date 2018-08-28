# Elasticsearch



操作方式:
1. 安装elasticsearch,去[官方](https://www.elastic.co/downloads/elasticsearch)下载安装包并按照步骤安装,运行bin/elasticsearch.bat(windows)

2. 安装 elasticsearch-head,类似于数据库的客户端软件(Navicat之于MySQL),在elasticsearch.yml中加入以下配置,以解决不同源的问题
    ```
        http.cors.enabled: true
        http.cors.allow-origin: "*"
        http.cors.allow-methods: OPTIONS, HEAD, GET, POST, PUT, DELETE
        http.cors.allow-headers: "X-Request-With, Content-Type, Content-Length,X-User"
    ```
    还可以安装kibana以后使用它的sense插件进行操作,类似于一个定制的控制台,可以直接发命令给es

3.