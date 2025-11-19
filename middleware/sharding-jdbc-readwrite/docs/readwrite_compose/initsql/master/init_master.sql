create database sharding_db;
use sharding_db;
create table user_balance
(
    user_id bigint         not null,
    balance decimal(10, 2) not null,
    primary key (user_id)
);

# slave账号进行主从的同步
# grant replication slave on *.* to 'slave'@'%' identified by 'slave';
# flush privileges;
# show master status;