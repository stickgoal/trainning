create database appdev;

use appdev;

CREATE TABLE `book` (
	`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '书籍主键',
	`book_name` VARCHAR(128) NOT NULL COMMENT '书名',
	`status` VARCHAR(20) NOT NULL COMMENT '书名',
	`create_date` TIMESTAMP NOT NULL COMMENT '创建日期',
	`up_time` TIMESTAMP NOT NULL COMMENT '创建日期',
	`stock` INT(11) NOT NULL COMMENT '库存量',
	`price` DECIMAL(10,2) NOT NULL COMMENT '库存量',
	PRIMARY KEY (`id`)
);