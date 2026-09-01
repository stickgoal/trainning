# Effective Java 并发技能

## When To Use

编写或评审多线程 Java 代码时：同步/可见性、锁、执行器、并发工具、线程安全文档、延迟初始化。

## Rules（按条目分组）

- **66 同步共享可变数据**：同步访问共享可变状态（互斥 + 可见性）；`volatile` 只用于可见性；原子操作用原子类。
- **67 避免过度同步**：锁内做最少工作；用开放调用（把外部/可覆盖方法调用移出锁）。
- **68 优先执行器**：用 `ExecutorService` 和线程池，不用裸 `Thread`；优雅关闭执行器。
- **69 优先并发工具**：用并发集合和同步器，不用 `wait`/`notify`；必须用时遵循 wait 循环惯用法，优先 `notifyAll`。
- **70 文档化线程安全**：标注不可变 / 无条件线程安全 / 有条件线程安全 / 非线程安全 / 线程敌对；说明哪个锁保护哪个状态。
- **71 延迟初始化谨慎**：优先急切初始化；否则用静态 holder、同步访问器，或 `volatile` 双重检查。
- **72 不依赖线程调度器**：避免忙等和过度订阅，不靠调度器保证正确性或性能。
- **73 不用线程组**（已废弃）。

## Common Mistakes（红旗）

- **共享可变字段无同步读写**：尤其是「写不加锁、读加锁」或反之。
- **`volatile` 用于复合操作**：如 `++` 不是原子的。
- **持锁调用回调/可覆盖方法**：可能死锁或长时间持锁。
- **无界线程创建**：库代码里 `new Thread(...)` 而非执行器。
- **`wait()`/`notify()` 脱离 wait 循环惯用法**。

## Examples

正确的懒加载（静态 holder 惯用法）：

```java
private static class Holder {
    static final Expensive INSTANCE = new Expensive();
}
public static Expensive get() {
    return Holder.INSTANCE;   // 类初始化时安全发布
}
```

## 关键提醒

- 在 Spring Boot 项目中，共享可变状态多来自：单例 Bean 的实例字段、静态缓存、`@Component` 里的集合。写代码前先识别「谁在并发访问这段状态」。
- 能用不可变对象、`ConcurrentHashMap`、原子类解决的问题，不要手写 `synchronized` 锁。
