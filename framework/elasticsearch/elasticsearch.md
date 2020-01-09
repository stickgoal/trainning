官方书籍：https://www.elastic.co/guide/cn/elasticsearch/guide/current/_navigating_this_book.html



- 主要概念

  - 搜索引擎

  - elasticsearch概念

    - 面向文档存储

    - 概念对应

      | 关系型数据库 | database | tables | rows      | columns |
      | ------------ | -------- | ------ | --------- | ------- |
      | ES           | indeices | types  | Documents | fields  |

    - 倒排索引

- 安装及基本使用

  - 安装elasticsearch

  - 安装kibana

  - 使用kibana操作elasticsearch

    - 创建索引

      ```bash
      PUT weather
      ```

    - 添加数据

      ```bash
      
      ```

      

    - 查询所有

      ```bash
      # 查询集群里的所有数据
      GET /_search
      
      # 查询索引weather的所有数据
      GET /weather/_search
      {}
      
      # 分页参数为 from 和size
      GET /_search
      {
        "from": 30,
        "size": 10
      }
      ```

    - 查询

    ```bash
    GET _search
    {
      "query": {
        "bool": {
          "should": [
            {
              "match": {
                "desc": "国"
              }
            },
            {
              "match": {
                "shortDesc": "国"
              }
            }
          ]
        }
      }
    }
    
    
    ```

    https://blog.csdn.net/gwd1154978352/article/details/82804942

- 集成到spring-boot
  - 选择依赖
  - 创建实体类（使用注解）
  - 创建仓储接口
  - 创建Controller
  - 通过@Query使用QueryDSL
- 项目应用
  - 数据库仍然使用myBatis，存入mysql数据库
  - 一般操作需要处理两个数据源，关系型数据库+ elasticsearch；插入，删除，更新都需要处理两个数据源
  - 普通业务操作使用数据库，全文搜索使用es

