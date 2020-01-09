

1. 搜索引擎使用的场景

   全文搜索，在整篇文章中搜索某个字或词，用关系型数据库实现必须使用like性能是极低的，速度非常慢

2. 搜索引擎的技术选型

​      Lucene  全文搜索引擎

​      =>  Apache Solr      老牌，文档非常丰富

​      =>  *ElasticSearch    新，rest api 使用广泛，聚合统计图形化方面比较突出  ELK ： elasticsearch   logstash   kibana   一套完整地日志收集、展示、搜索的技术栈



3. ElasticSearch基本介绍

   https://www.elastic.co/cn/

   开源，免费

   - 存储：

     -  面向文档，json格式的文档存储

     - schema free，不需要像关系型数据库一样定义结构

       ```json
       {
           "email": "john@smith.com",
           "first_name": "John",
           "last_name": "Smith",
           "info": {
               "bio": "Eco-warrior and defender of the weak",
               "age": 25,
               "interests": [
                   "dolphins",
                   "whales"
               ]
           },
           "join_date": "2014/05/01"
       }
       ```

   - elasticsearch的基本概念

     | 关系型数据库  | database数据库 | tables  表   | rows   行       | columns 列   |
     | ------------- | -------------- | ------------ | --------------- | ------------ |
     | elasticsearch | indeices索引   | types   类型 | Documents  文档 | fields  字段 |

用户： index    张三 ： {name:"张三"...}    name  : name

区分index词的含义，索引

- 名词，表示一个类型的所有文档的集合
- 动词，将某个数据加入到对应的索引中
- 倒排索引，数据结构

4. 安装

   - 解压缩
   - cmd 打开 bin/elasticsearch.bat
   - 访问localhost:9200 看到Json输出即正确
   - kibana安装
     - 解压缩
     - cmd 打开 bin/kibana.bat
     - 访问 localhost:5601

5. 使用kibana 操作elasticsearch

   REST API  ： 通过HTTP请求方法表示操作，get 获取数据，  post 新增数据  put更新数据 delete 删除数据

   ```bash
   # 创建索引
   PUT /emall
   
   # 查询所有数据
   GET /emall/_search
   
   # 添加/更新数据
   POST /emall/product/1
   {
     "name":"苹果",
     "info":"正宗红富士",
     "decription":"自产自销，不甜包退"
   }
   
   POST /emall/product/2
   {
     "name":"iphone",
     "info":"苹果新款手机",
     "decription":"现货现发，不甜包退"
   }
   
   POST /emall/product/3
   {
     "name":"冰糖雪梨",
     "info":"饮料，甜品",
     "decription":"绝对比水果甜，不甜包退"
   }
   
   # 根据ID查看某一条数据
   GET /emall/product/3
   
   # 搜索
   GET /emall/product/_search
   {
     "query":{
       "match":{"name":"苹果"}
     },
     "highlight":{
       "fields":[{"name":{}}]
     }
   }
   
   GET /emall/product/_search
   {
     "query":{
       "bool":{"should":[
           {"match":{"name":"苹 果"}},
           {"match":{"info":"苹 果"}},
           {"match":{"decription":"苹 果"}}
         ]}
     },
     "highlight":{
       "fields":[{"name":{}},{"info":{}},{"decription":{}}]
     }
   }
   
   GET /emall/product/_search
   # 删除某条数据
   DELETE /emall/product/3
   # 删除整个索引
   DELETE /emall
   
   ```

   6. java开发集成elasticsearch

      spring-data-*   :  spring-data-jpa / spring-data-redis /spring-data-elasticsearch

      