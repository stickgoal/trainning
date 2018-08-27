# lombok 代码简洁神器

lombok通过一些注解,去掉惹人厌烦的重复代码,如setter,getter,log等,手指福音

操作方法:
1. 装插件
2. 加jar包
3. 类上加注解

### 1. 安装插件
- idea比较简单,在settings>plugins>Browser Repositories> 直接搜索lombok,然后安装 重启
- eclipse 从[官网](https://projectlombok.org/download)下载jar包, cmd中执行`java -jar lombok.jar` 安装,指定eclipse.exe的安装位置即可

### 2. 加jar包

### 3. 加注解
常用注解:
- @Data 标记为数据类,自动生成setter getter toString equals hashcode方法
- @AllArgsConstructor 所有参数的构造器
- @NoArgsConstructor 没有参数的构造器
- @RequiredArgsConstructor 加了@NonNull(不是NotNull哦)注解的字段生成构造器
- @Slf4j 直接生成slf4j的log
- @SneakyThrows,将方法中的代码用try-catch包裹
比如
```java

     @SneakyThrows(RuntimeException.class)
     public String  doSomethingDangerous(){
        return "x";
     }
    ===>
     public String doSomethingDangerous() {
        try {
            return "x";
        } catch (RuntimeException var2) {
            throw var2;
        }
    }
```
 - 更多特性请见[官网](https://projectlombok.org/features/)