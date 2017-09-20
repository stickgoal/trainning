/*模块化*/
'use strict';

var s = 'hello';

function greet(name){
	console.log(s+' , '+name);
}

module.exports={s:greet};