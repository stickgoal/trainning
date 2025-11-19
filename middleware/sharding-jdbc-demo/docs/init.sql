# use sharding_jdbc_demo;
# create table user_balance(
#                                user_id bigint not null,
#                                balance decimal(10,2) not null,
#                                primary key (user_id)
# );

create database ds0;
use ds0;


CREATE TABLE `user_user_login`
(
    `user_id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `login_name`         varchar(20) NOT NULL COMMENT '登录名',
    `login_password`     varchar(512)         DEFAULT NULL COMMENT '登录密码',
    `user_status`        tinyint     NOT NULL COMMENT '登录状态',
    `login_mobile`       varchar(20) NOT NULL COMMENT '手机号',
    `default_login_type` tinyint     NOT NULL DEFAULT '0' COMMENT '默认登录方式,0：密码',
    `create_time`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime    NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户登录表';

create table user_balance_0(
  user_id bigint not null,
  balance decimal(10,2) not null,
  primary key (user_id)
);
create table user_balance_1(
                               user_id bigint not null,
                               balance decimal(10,2) not null,
                               primary key (user_id)
);
create table user_balance_2(
                               user_id bigint not null,
                               balance decimal(10,2) not null,
                               primary key (user_id)
);



create database ds1;
use ds1;


create table user_balance_0(
                               user_id bigint not null,
                               balance decimal(10,2) not null,
                               primary key (user_id)
);
create table user_balance_1(
                               user_id bigint not null,
                               balance decimal(10,2) not null,
                               primary key (user_id)
);
create table user_balance_2(
                               user_id bigint not null,
                               balance decimal(10,2) not null,
                               primary key (user_id)
);

CREATE TABLE `user_user_login`
(
    `user_id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `login_name`         varchar(20) NOT NULL COMMENT '登录名',
    `login_password`     varchar(512)         DEFAULT NULL COMMENT '登录密码',
    `user_status`        tinyint     NOT NULL COMMENT '登录状态',
    `login_mobile`       varchar(20) NOT NULL COMMENT '手机号',
    `default_login_type` tinyint     NOT NULL DEFAULT '0' COMMENT '默认登录方式,0：密码',
    `create_time`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime    NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户登录表';