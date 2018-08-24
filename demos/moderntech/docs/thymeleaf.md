#thymeleaf

spring官方支持的后端模板引擎,应该值得投资.

简单试用了下,特点在于自然模板,也就是模板即原型.
为了实现这个目标,所有的模板都是用html的属性来实现,浏览器会忽略,所以可以直接显示,而模板引擎会解析这些属性.两者兼顾

示例代码:me.maiz.demo.moderntech.thymeleaf 及 moderntech\src\main\resources\templates

操作方法:
- 引入starter
- 后台加入数据,模板渲染

- 取值方式 
    - ${user} 用于取值及运算,比如字符串的运算
    - \#{user} 用于国际化
    - \*{user} 用于在某个上下文中取值(比如循环中)
    - @{link}用于 显示连接

- 判断

- 循环