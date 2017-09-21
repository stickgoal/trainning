#前端构建工具介绍

- webpack
- 其他常见工具趋势



### webpack

前端越来越高级，自然就越来越复杂，如果手动应对这个复杂性，那么只会令越来越疲劳和无聊。所以就出现了自动化构建工具。

这些构建工具可以类比java世界中的maven，，maven的构建流程本身可以加入各种插件，实现各类重复手动的功能，比如将JVM的其他语言（如日中天的Kotlin）编译成java或者class，或者将 java源码编译成class并且打包等等。前端的这类工作也有，比如，把coffeeScript编译成js代码或者执行压缩，把js压缩成min.js减少体积等等。

那么这些工具怎么玩呢？

说实话胆战心惊，因为我对前端也是小白，深度不够，只能是概览一下，留下个印象。这里以webpack为例。

有条件直接看webpack[官方文档](https://doc.webpack-china.org/concepts/) 。

#### 什么是webpack

![webpack_concept](https://webpack.github.io/assets/what-is-webpack.png)

将一堆各种格式的文件[*输入*]转换成最终的产出物[*输出*]。

这个过程中存在分析，加载，转换，输出等步骤。

##### 概念

- 入口：指定从哪个文件开始查找
- 输出：打包后最终生成的文件
- 加载器：loader 加载各种资源并转化成统一的方式
- 插件：实现功能的组件

##### 如何使用

待续