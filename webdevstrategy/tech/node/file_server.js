/**
*简单文件服务器，传入需要下载的文件，响应对应的文件
*/
'use strict';


var fs = require('fs');
var url = require('url');
var path = require('path');
var http = require('http');

console.log(url.parse('http://user:pass@host.com:8080/path/to/file?query=string#hash'));

var localPath = path.resolve('c:/tmp/');
console.log("文件服务本地路径："+localPath);

// 创建http服务器
var server = http.createServer(function(request,response){
	//解析请求的URL，获取其中的pathname属性，也就是请求路径部分
	var pathname = url.parse(request.url).pathname;
	console.log("请求的路径名 : "+pathname);
		//如果请求是/则查找是否有index.html，如果有则返回index.html
		scanIndex(localPath,pathname,function(newPathname){
			//排除favicon的请求
			if(newPathname!='/favicon.ico'){
					//构造本地文件路径
					var file = path.join(localPath,newPathname);
					console.log('本地路径：'+file);
					fs.stat(file,function(err,stats){
						//判断是否存在或者是否为文件
						if(!err && stats.isFile()){
							response.writeHead(200,{'Content-Type':'text/html;charset=utf-8'});
							// 读取文件流并通过管道转接给响应
							fs.createReadStream(file).pipe(response);	
						}else{
							console.log('文件'+file+'不存在');
			            	response.writeHead(404,{'Content-Type': 'text/plain;charset=utf-8'});
			            	response.end('404 您请求的文件'+newPathname+'不存在');
						}	
					});
					
				}
		});

	
}).listen(8000);

console.log("server is running on localhost:8000");

/**
*查找index.html，不能按照同步的写法，因为access方法为异步方法， 必须使用回调来处理。
*/
function scanIndex(localPath,pathname,fun){

		if(pathname==='/'){
			var index = path.join(localPath,'index.html');
			console.log('尝试查找'+index);
			fs.access(index, fs.constants.R_OK , (err) => {
				if(!err){
					fun('/index.html');
				}
			 	
			});
		}else{
			fun(pathname);
		}
	


}