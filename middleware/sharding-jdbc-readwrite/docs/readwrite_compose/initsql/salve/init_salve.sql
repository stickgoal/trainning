create database sharding_db;
use sharding_db;
create table user_balance
(
    user_id bigint         not null,
    balance decimal(10, 2) not null,
    primary key (user_id)
);

#以下手动执行
# 重制主从
reset slave;
# 配置要同步的主库
change master to master_host= 'master', master_user='slave', master_password='slave',
   master_log_file='mysql-bin.000003', master_log_pos=590;
# 开始同步
start slave;
# 查看状态
show slave status;
#
# # 添加read账号
# grant select  on *.* to 'read'@'%' IDENTIFIED by 'read';
# flush PRIVILEGES;