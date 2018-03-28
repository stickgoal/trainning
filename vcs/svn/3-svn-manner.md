# svn manner 操作规范及礼节

大体原则：避免冲突，尊重他人

- 多人合作时，应当养成多提交和多更新的习惯。至少保证每日早中晚各更新及提交一次
- 每次提交前先更新，代码正常后才可以提交
  - 更新，确认没有冲突后，本地运行更新后的代码，没有问题后再提交
  - 若是有冲突则解决冲突
  - 若是本地运行有问题，则解决问题后，再提交。否则合作者更新你的提交后无法继续工作
- 冲突发生时需要分析和交流，不可以直接覆盖他人提交，涉及修改他人代码则与对方直接交流，商讨过后再确定如何解决
- 提交**必须**写message，概括本次提交的主要内容，比如，“添加了用户注册功能”；或者“修改用户重复注册的bug”，做到见message知提交内容。强烈反对写：“新增UserService.java,UserDAO.java”这类废话



- 为避免本地数据冲突，需要做如下配置：

  - 在任意目录右击，选择`tortoiseSVN` > `settings` >`general`>`Global ignore pattern`中添加如下内容：

    ```
    velocity.log* bin target .settings .classpath .project *.o *.lo *.la *.al .libs *.so *.so.[0-9]* *.a *.pyc *.pyo *.rej *~ #*# .#* .*.swp .DS_Store .springBeans .idea *.iml
    ```

  - 在服务器端删除pom.xml和src外的其他生成代码，比如eclipse的.classpath 和 .project文件等

  - 客户端从eclipse中删除项目，更新后重新导入到eclipse中