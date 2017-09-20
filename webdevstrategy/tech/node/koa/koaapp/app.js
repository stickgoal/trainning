const Koa = require('koa');

const router = require('koa-router')();


const app = new Koa();

//拦截请求
app.use(async (ctx, next) => {
    console.log(`Process ${ctx.request.method} ${ctx.request.url}...`);
    await next();
});

router.get('/books/:bookId',async(ctx,next)=>{
    
    var bookId = ctx.params.bookId;
    
    ctx.response.body='您要找的书的ID为'+bookId;
});

router.get('/',async (ctx,next)=>{
    console.log(ctx);
    ctx.response.body='<h1>首页</h1>'
});

app.use(router.routes());

app.listen(3000);
console.log('server running at http://localhost:3000');