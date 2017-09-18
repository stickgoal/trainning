/*演示文件读写模块fs*/
'use strict';

var fs = require('fs');

try{
// 同步读取文件
var data = fs.readFileSync('sample.txt','utf-8');
console.log(data);
// 报异常
fs.readFileSync('sample1.txt','utf-8');

}catch(err){
	// 捕获异常并处理
	console.error('读取文件出错：\n'+err);
}

//写文件
var data2 ="hello node";
fs.writeFile('output.txt',data2,function (err) {
	if(err){
		console.error(error);
	}else{
		console.log('ok');
		console.log(fs.readFileSync('output.txt'));
	}
});

// 统计信息
fs.stat('output.txt',function(err,stat){
	if(err){
		console.error(error);
	}else{
		console.log("名字\t目录\t大小\t创建日期\t修改日期");
		console.log("output.txt\t"+stat.isDirectory()+"\t"+stat.size+"\t"+stat.birthtime.toLocaleString()+"\t"+stat.mtime.toLocaleString());
	}
});
