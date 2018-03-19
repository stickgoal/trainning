 # springmvc 知识点

 - SpringMVC
     - 核心概念
         - 中心控制器模式
         	 - 处理流程
         	 - MVC组件
         		 - View
         		 - ViewResolver
         		 - ModelAndView
         		 - Controller
         		 - HandlerMapping
          - 用法
             - 配置

              - @Controller @RequestMapping

                   - modelMap的使用
                  - GET / POST  method
                  - @PathVariable

              - 路径分级 ContextLevelController

              - 页面跳转 RedirectingController

              - 数据绑定及验证  DataBindingConversionController

              - 数据类型转换及自定义 DataBindingConversionController

              - 静态资源处理

                  ```xml
                  <!-- 静态资源处理 -->
                  	<mvc:resources location="/WEB-INF/js/" mapping="/js/**" />
                  	<mvc:resources location="/WEB-INF/css/" mapping="/css/**" />
                  	<mvc:resources location="/WEB-INF/img/" mapping="/img/**" />
                  	<mvc:resources location="/WEB-INF/html/" mapping="/html/**" />
                  ```

              - 异常处理   GlobalExceptionHandler

              - 文件上传支持 UploadController(添加commons依赖)

                  ```xml
                  <!-- 文件上传 -->
                  	<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
                  		<property name="maxUploadSize" value="2097152"/>
                  	</bean>
                  ```

              - JSON转换  JSONController、RestfulController

                   - @ResponseBody  @RestController

              - 与spring集成

                   - 使用默认配置文件名称

                        - 默认使用DispatcherServlet的servlet-name,再加上-servlet.xml作为配置文件，如springmvc-servlet
                        - springmvc的父容器，是一个spring容器，默认使用application-context.xml作为配置文件名，可以通过设置名为contextConfigLocation的context-param标签来修改

                   - component-scan的处理

                        - applicationContext.xml配置中：

                            ```xml
                            <!-- 包扫描 -->
                            	<context:component-scan base-package="our.package.name">
                            		<context:exclude-filter type="annotation"
                            			expression="org.springframework.stereotype.Controller" />
                            	</context:component-scan>
                            ```

                            ​

                        - ​

                  ​

                  ​

                  ​

                  ​	