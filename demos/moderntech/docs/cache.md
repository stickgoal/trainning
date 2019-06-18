# spring-cache

示例见:me.maiz.demo.moderntech.cache
这里主要是试用了cache的几个注解

操作方法:
1. Application上加入@EnableCaching注解
2. 数据操作方法上加入注解,调用

解释注解:
- @Cacheable,用于增加缓存, 一般用法是@Cacheable("缓存名字"),返回值会被缓存起来
    - 缓存的key就是参数(单个参数时就是这个参数,没有参数是`SimpleKey.EMPTY`,多个参数时,组合起来生成一个`SimpleKey`)
    - 后续操作时会不调用方法,直接返回缓存的值
- @CacheEvict,用于删除缓存,有个问题是缓存的key必须在参数中.属性 allEntries=true时,全部清空
- @CachePut,用于更新某个缓存,需要注意的是返回值会被放入缓存,示例用List就不太合理了
- @CacheConfig,加在类上,整个类范围内的所有方法都会共享这些配置,比如这里的cacheName,所有的操作都是针对一个缓存名字了

解释示例:

示例里代码me.maiz.demo.moderntech.cache.CacheTestController.testCache,第一次调用耗时5秒,后续的调用几乎都是0

remove后,重新调用,第一次仍然是5秒

add后,虽然add方法没有任何实现,但是缓存会更新,再去查询就是更新后的值了,这里看到缓存对结果的影响很直接还是需要谨慎使用.处理不当可能出现持久化数据与缓存数据不一致的情况.
