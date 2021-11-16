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

    - 管理索引

      ```bash
      # 创建索引
      PUT /weather
      ```
    # 删除索引
      DELETE /weather
    # 删除所有索引，慎用
      DELETE /*
    
      ```
    
      ```
    
  - 添加数据
    
    ```bash
      POST /weather/weather
    {
        "id":1,
        "info":"阴天",
        "temp":"23°C"
      }
      POST /weather/weather
      {
        "id":2,
        "info":"晴天",
        "temp":"22"
      }
      #或者
      PUT /weather/weather/3
      {
        "info":"晴朗",
        "temp":"23°C"
    }
    ```

    - 数据操作
    
      ```bash
      #添加
      POST /weather/weather/1
      {
        "info":"阴天",
        "temp":"23°C"
      }
      POST /weather/weather/2
      {
        "info":"晴天",
        "temp":"22"
      }
      POST /weather/weather/3
      {
        "info":"多云转晴",
        "temp":"22"
      }
      # 查询所有
      GET /weather/_search
      # 查询某个信息
      GET /weather/weather/1
      ```
    # 删除某个信息
      DELETE /weather/weather/3
      ```
    
    - 搜索
    
    ​```bash
    GET /weather/weather/_search 
    { "query" : { "match" : { "info" : "晴" } } }
    
    PUT /emall
    
    POST /emall/product/1
    {
      "name":"苹果",
      "info":"正宗红富士",
      "description":"不甜不要钱"
    }
    
    POST /emall/product/2
    {
      "name":"iphone",
      "info":"苹果新款，现货现发",
      "description":"不甜不要钱"
    }
    
    POST /emall/product/3
    {
      "name":"冰糖雪梨",
      "info":"冰糖雪梨饮料",
      "description":"比苹果还要甜"
    }
    
    GET /emall/product/_search 
    
    GET /emall/product/_search 
    {
      "query": {
        "bool": {
          "should": [
            {
              "match": {
                "name": "苹果"
              }
            },
            {
              "match": {
                "info": "苹果"
              }
            },
            {
              "match": {
                "description": "苹果"
              }
            }
          ]
        }
      },
      "highlight": { "fields" : { "name" : {} ,"info":{},"description": {}} }
    }
    
      ```
    
  - 

- 集成到spring-boot
  - 选择依赖
  - 创建实体类（使用注解）
  - 创建仓储接口
  - 创建Controller
  - 通过@Query使用QueryDSL

- 项目应用