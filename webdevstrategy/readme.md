# 前端工程师应聘攻略

本文编写的目的是为了帮助java培训中的学生在面试中找到前端工程师的工作。文章内容同样适用于web初学但想应聘前端工程师岗位的同学以及希望提升自己web技术水平的同学。

###大纲

1. 明确前端工程师岗位要求
2. Web基础技术概览及预备建议
3. 浏览器兼容性处理预备建议
4. 流行框架入门及学习建议
   - vue.js
   - node.js
   - angular.js
   - react.js
   - 其他
5. 前端构建流程详解
6. 项目积累



### 1.明确前端工程师岗位要求

如何知道当前区域对前端工程师的岗位要求呢？最直接的就是看招聘网站。以重庆为例，分别于51job、智联招聘搜索前端开发工程师，查看岗位职责，抓取共同点，得到基本的要求。并据此根据实际情况进行准备。

详情见[搜集的资料](webdev_reqirement.md)

从各家的招聘需求可以看出，各家对前端工程师的要求各不相同，有侧重UI和美工，有侧重后端部分。但关于前端部分的要求有几个共同点：

- web基础知识的掌握（HTML\CSS\JS）
- 浏览器兼容性
- 当前流行框架的了解及应用
- 具备一定的项目经验

显然，作为前端开发要了解前端的常用技术，应付常用问题，使用流行的框架是为了生产力的提高。而项目经验则是最为重要的实践经历参考了。

### 2.Web基础技术概览及预备建议

web的基础技术由三大部分组成

- HTML 		一种标记语言，提供内容的描述，比如对标题、文本、列表、表格等内容组件的描述
  - CSS          一种样式语言，提供外观的描述，包括整体外观（布局技术）和细节外观（样式）
- JAVASCRIPT    一种编程语言，操作前两者实现动态的内容效果，基于事件编程模式

三部分学习建议：

1. 跟着[w3school的教程](http://www.w3school.com.cn/html/index.asp)把HTML学一遍，HTML是最简单的，不需要过多说明。
2. 先可以跟着[ 菜鸟教程的CSS教程](http://www.runoob.com/css/css-tutorial.html)学习下CSS，[学习布局知识](http://zh.learnlayout.com/)。推荐[ 李炎恢老师的视频](http://study.163.com/course/introduction/1003005.htm)
3. JS建议学习[MDN的教程](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript) ，如果英文实在困难，再考虑[菜鸟](http://www.runoob.com/js/js-tutorial.html)

如果有同学已经学过了，那么给出复习建议：

1. 哪里不熟补哪里，回去翻看相关的知识
2. css [知识点](http://www.imooc.com/article/2235) [布局练习](http://www.jianshu.com/p/b314f9915a00)   
3. [js练习](http://www.mhero.com/) 

关于重点：

​	HTML的重点在于对于常用标签的属性的掌握，比如\<img src=''>

​	CSS的重点有选择器、样式、布局技巧

​	javascript部分重要是多写，多练，使用原生JS编写页面效果，编写组件等等

网上找的类似参考资料 : 

[从草根到英雄系列](http://www.jianshu.com/p/8e639ae10dfe)



###3.浏览器兼容性处理预备建议 

这个问题的出现是因为各浏览器对web标准支持不一致导致的，但现在现代浏览器已经趋于统一。所以基本上可以熟悉下常见的浏览器兼容性问题，弄懂其中至少一个css一个js问题，对这类问题有个概念。

至于实际的处理，建议参考知乎的回答，把浏览器分成遗留浏览器和现代浏览器，考虑现代浏览器再考虑fallback到遗留浏览器

[也谈兼容性——通用hack方法/CSS兼容方案/js兼容方案全推送](https://zhuanlan.zhihu.com/p/25123086?refer=dreawer)

[常见浏览器兼容性问题与解决方案](http://blog.csdn.net/chuyuqing/article/details/37561313/)

[Javascript 多浏览器兼容性问题及解决方案](http://www.jb51.net/article/21483.htm)

[ 知乎：怎样可以很好地保证网页的浏览器兼容性？](https://www.zhihu.com/question/19736007)



### 4.流行框架入门及学习建议

前端框架的层出不穷让人汗颜，jquery已经早已是过去式，甚至angularjs的风头也要被react\vue替代。这个部分需要掌握react/vue中的一个，了解下angular和node都有好处。

#### vue.js

个人认为学习入门最好是看官方文档。vue的官方文档是比较齐全的，看一遍就可以掌握基础了。

vue 读音与view相同，可见他的重点在于构建页面，但是实际上包含了一个前端框架该有的各个部分。这篇[浅谈vuejs](http://www.cnblogs.com/luozhihao/p/5329440.html) 也可以参考。

参考官网做的一些[例子](frameworks\vue\),可以参考