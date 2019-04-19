# thymeleaf

spring官方支持的后端模板引擎,应该值得投资.

简单试用了下,特点在于自然模板,也就是模板即原型.
为了实现这个目标,所有的模板都是用html的属性来实现,浏览器会忽略,所以可以直接显示,而模板引擎会解析这些属性.两者兼顾

示例代码:me.maiz.demo.moderntech.thymeleaf 及 moderntech\src\main\resources\templates
    
    - home.html是简单使用
    - page.html是一个简单的分页

操作方法:
- 引入starter
- 后台加入数据
- 页面在HTML标签中加入`xmlns:th="http://www.thymeleaf.org"`, 就可以加入th属性了

- 取值方式 
    - ${user} 用于取值及运算,比如字符串的运算|你是${user.role}|
    - \#{user} 用于国际化
    - \*{user} 用于在某个上下文中取值(比如循环中)
    - @{link}用于 显示连接

- 判断 使用th:if属性
    - 注意为元素添加class要用th:class加三元表达式完成;
    - 其他HTML属性都有相应的th:属性与之对应;
    - 有th:xxxappend继续添加某属性的值,比如th:class定义类,th:classappend添加其他类
- 循环 使用th:each属性
    ```th:each="b :${books}"```
- 内置对象,用于格式化等 ${#numbers.sequence(0,6)}

- layout