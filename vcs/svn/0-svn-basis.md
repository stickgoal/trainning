# svn basis 基本概念

SVN是一个VCS(version control system)工具， 管理随时间改变的数据。便于集中管理（质量检查、权限控制、代码复用）和追溯代码。

###基本概念

- 代码库 repository

  代码使用一个中央服务器集中管理，代码被提交到服务器保存，其他客户端即可读写此类代码。同时解决了代码备份问题。

  ![-typical-client-server-syste](static\a-typical-client-server-system.png)

  代码库包含一个网络地址，如`http://svn.example.com:9834/repos` ,客户端可以使用该地址对代码库操作。

  多个客户端对同一份代码更改时，svn尝试自动合并，若不能，则提示冲突，要求解决冲突后再提交。

- 检出 checkout

  第一次从服务器下载代码库

- 更新 update

  使用服务器代码库更新本地工作副本

- 提交 commit

  客户端对服务器的一次有效修改

- 版本  Revisions

  ​	一次任何形式的提交会在服务器端形成一个更改，称为revision，每个版本独立存在，可以检出某个revision的代码。同时每个文件revision集中起来就是该文件的修改历史。

  ​	![tree-changes-over-time](static\tree-changes-over-time.png)

- 工作副本  working copy

  客户端本地的代码库，可能被修改而与repository不一致。通过更新可以得到服务器端最新的更改，通过提交操作可以将本地更改提交到服务器端。