# 初识微信小程序

微信小程序号称可以不安装就直接运行，立即想到WebAPP，但是细想之下这个所谓的小程序运行在微信中而不是浏览器中，所以微信可以开放一些能力给它，就可以实现WebAPP实现不了的事情了。（这就有点Hybird的意思了）

大致看了下微信小程序的[文档](https://mp.weixin.qq.com/debug/wxadoc/dev/) 稍微阐释下。

- 技术栈

  - js    =>   js代码，不用说控制逻辑
  - wxss => 样式代码，类似css
  - wxml => 页面结构，类似于HTML
  - json => 配置文件

- 约定

  - 一个页面的js\wxss\wxml\json必须同名，所以就不存在引入样式的需要了
  - 入口页面为app,也就是默认调用app相关文件来展示页面

- 功能扩展

  - js方面是微信开放的API，参见[文档](https://mp.weixin.qq.com/debug/wxadoc/dev/api/)

  - 页面展示方面，微信提供了一些组件可以使用，参见[文档](https://mp.weixin.qq.com/debug/wxadoc/dev/component/)

    ​

  ​