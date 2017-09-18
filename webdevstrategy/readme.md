# 前端工程师应聘攻略

本文编写的目的是为了帮助java培训中的学生在面试中找到前端工程师的工作。文章内容同样适用于web初学但想应聘前端工程师岗位的同学以及希望提升自己web技术水平的同学。

### 大纲

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
3. JS基础建议学习[MDN的教程](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript) ，如果英文实在困难,可以考虑廖雪峰的[js教程](https://www.liaoxuefeng.com/wiki/001434446689867b27157e896e74d51a89c25cc8b43bdb3000) 或者阮一峰的[js标准教程](http://javascript.ruanyifeng.com/)  ，实在不行再考虑[菜鸟](http://www.runoob.com/js/js-tutorial.html)

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



### 3.浏览器兼容性处理预备建议 

这个问题的出现是因为各浏览器对web标准支持不一致导致的，但现在现代浏览器已经趋于统一。所以基本上可以熟悉下常见的浏览器兼容性问题，弄懂其中至少一个css一个js问题，对这类问题有个概念。

至于实际的处理，建议参考知乎的回答，把浏览器分成遗留浏览器和现代浏览器，考虑现代浏览器再考虑fallback到遗留浏览器

[也谈兼容性——通用hack方法/CSS兼容方案/js兼容方案全推送](https://zhuanlan.zhihu.com/p/25123086?refer=dreawer)

[常见浏览器兼容性问题与解决方案](http://blog.csdn.net/chuyuqing/article/details/37561313/)

[Javascript 多浏览器兼容性问题及解决方案](http://www.jb51.net/article/21483.htm)

[ 知乎：怎样可以很好地保证网页的浏览器兼容性？](https://www.zhihu.com/question/19736007)



### 4.流行框架入门及学习建议

前端框架的层出不穷让人汗颜，jquery已经早已是过去式，甚至angularjs的风头也已被react\vue替代。这个部分需要掌握react/vue中的一个，了解下node，熟悉前端的构建体系是必须的，都有好处。

#### vue.js

> Vue.js (读音 /vjuː/，类似于 **view**) 是一套构建用户界面的**渐进式框架**。与其他重量级框架不同的是，Vue 采用自底向上增量开发的设计。Vue 的核心库只关注视图层，它不仅易于上手，还便于与第三方库或既有项目整合。另一方面，当与[单文件组件](https://cn.vuejs.org/v2/guide/single-file-components.html)和 [Vue 生态系统支持的库](https://github.com/vuejs/awesome-vue#libraries--plugins)结合使用时，Vue 也完全能够为复杂的单页应用程序提供驱动。[引自[vue中文官网](https://cn.vuejs.org/v2/guide/index.html)]

个人认为学习入门最好是看官方文档。vue的官方文档是中文的而且比较齐全，看一遍就可以掌握基础了。

vue 读音与view相同，可见他的重点在于构建页面，但是实际上包含了一个前端框架该有的各个部分。这篇[浅谈vuejs](http://www.cnblogs.com/luozhihao/p/5329440.html) 可以作为参考。

阅读官网做的一些 [例子](tech/vue/index.html),可以参考。以下为官网文档极度减缩版。

##### 声明式渲染

最基本的声明式渲染非常容易理解

```vue
<div id="app">
  {{ message }}
</div>
```

```js
var app = new Vue({
  el: '#app',
  data: {
    message: 'Hello Vue!'
  }
})
```

html部分是一个模板，new Vue创建一个Vue实例，传入配置对象，el属性表示选择需要渲染的对象，data表示要渲染进去的数据。直观！

绑定属性就用v-bind:属性名=“变量名”，变量名在创建Vue实例时赋值

```vue
<div id="app-2">
  <span v-bind:title="message">
    鼠标悬停几秒钟查看此处动态绑定的提示信息！
  </span>
</div>
```

```js
var app2 = new Vue({
  el: '#app-2',
  data: {
    message: '页面加载于 ' + new Date().toLocaleString()
  }
})
```

属性插值也可以简写为 `  <span :title="message">`

另外，对于class和style、循环（v-for）、判断(v-if)等都有相应的处理。


##### 事件绑定

对于事件系统，vue提供了极为简化的处理

```vue
<div id="example-2">
  <!-- `greet` 是在下面定义的方法名 -->
  <button v-on:click="greet">Greet</button>
</div>

```

```js

var example2 = new Vue({
  el: '#example-2',
  data: {
    name: 'Vue.js'
  },
  // 在 `methods` 对象中定义方法
  methods: {
    greet: function (event) {
      // `this` 在方法里指当前 Vue 实例
      alert('Hello ' + this.name + '!')
      // `event` 是原生 DOM 事件
      if (event) {
        alert(event.target.tagName)
      }
    }
  }
})
// 也可以用 JavaScript 直接调用方法
example2.greet() // => 'Hello Vue.js!'
```

也可以简写为`  <button @click="greet">Greet</button>`

甚至对于键盘鼠标都提供了后缀，简洁的不要不要的。

```vue
<!-- 只有在 keyCode 是 13 时调用 vm.submit() -->
<input v-on:keyup.13="submit">
```



#####  组件

vue最大的特点就是组件化，简直无敌。web页面其实都可以被分解成一个个的组件，

![组件化](https://cn.vuejs.org/images/components.png)

一个组件化的应用可能会是这样的

```html
<div id="app">
  <app-nav></app-nav>
  <app-view>
    <app-sidebar></app-sidebar>
    <app-content></app-content>
  </app-view>
</div>
```

vue提供了简单的方式定义组件

```js
// 注册
Vue.component('my-component', {
  template: '<div>A custom component!</div>'
})
// 创建根实例
new Vue({
  el: '#example'
})
```

用法

```vue
<div id="example">
	<!--以下为使用组件的代码-->
  <my-component></my-component>
</div>
```

组件自然需要支持嵌套，父子组件之间可以通信。

> Vue 组件的 API 来自三部分 - props, events 和 slots ：
>
> - **Props** 允许外部环境传递数据给组件
> - **Events** 允许从外部环境在组件内触发副作用
> - **Slots** 允许外部环境将额外的内容组合在组件中。

简单的过一下vue，vue给我的感觉就像当年接触到jquery，发觉js竟然可以这么简洁。vue让前端开发如此简洁。显然的，前端的时代到来了。

如果说原生js是javase阶段，vue已经到达了三大框架的程度，那些vue-cli这类工具和spring-boot-cli几乎是等价了。构建工具方面，前端的grunt,bower和maven、gradle也是相当的。甚至node的出现，都跟spring-boot把tomcat内嵌了有些相似，不再需要外部的运行环境而可以独立运行了。

#### node.js

node.js不是一个js库，而是一个运行时环境，就是让js脱离浏览器运行，那么js就变成了一门可以使用在任何场景的编程语言。接收网络请求，读写文件，处理二进制内容这类本来不可以做的事情现在都可以做了。js变成了完整的服务端语言。正因为nodejs是一个运行时环境，js本身的语法是可以直接用的。

> Node.js 是一个基于 Chrome V8 引擎的 JavaScript 运行环境。 
> Node.js 使用了一个事件驱动、非阻塞式 I/O 的模型，使其轻量又高效。 
> Node.js 的包管理器 npm，是全球最大的开源库生态系统。
>
> 引自[node中文网](http://nodejs.cn/)

建议学习廖雪峰老师的[教程](https://www.liaoxuefeng.com/wiki/001434446689867b27157e896e74d51a89c25cc8b43bdb3000/001434501245426ad4b91f2b880464ba876a8e3043fc8ef000) 或者阮一峰的[教程](http://javascript.ruanyifeng.com/nodejs/basic.html#toc4) （虽然并未公开）或者[node入门](https://www.nodebeginner.org/index-zh-cn.html) 电子书

##### 安装教程

简单来说就是去官网下载，并且逐步安装。参考[安装教程](http://www.runoob.com/nodejs/nodejs-install-setup.html) 。执行命令`node -v` 或者`npm -v` 出现版本号就是胜利。

##### 简单示例

创建一个webserver的[helloworld](tech/node/helloworld.js),但请用`node helloworld.js`运行之。

##### 文件操作

