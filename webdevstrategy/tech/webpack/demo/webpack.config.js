var webpack = require('webpack');
var commonsPlugin = new webpack.optimize.CommonsChunkPlugin('common');

module.exports={
    //插件
    plugins:[commonsPlugin],
    //入口文件
    entry:{
        main:'./src/js/main.js',
        p1:'./src/js/pages/p1.js',
        p2:'./src/js/pages/p2.js'
        },
    //输出
    output:{
        path:__dirname+'/dist/js',
        filename:'[name].js'
    },
    module:{
         //加载器配置
         loaders: [
            { test: /\.css$/, loader: 'style-loader!css-loader' },
            { test: /\.(png|jpg)$/, loader: 'url-loader?limit=8192'}
        ]
    },
    // 告诉webpack这是node环境
    target:'node'


}
