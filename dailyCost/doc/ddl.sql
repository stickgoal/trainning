CREATE TABLE `dc_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `direction` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `userId` int(11) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dc_record` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `direction` varchar(255) DEFAULT NULL,
  `keepTime` datetime DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  `occurTime` datetime DEFAULT NULL,
  `userId` int(11) NOT NULL,
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dc_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

select * from dc_user;

insert into dc_user(email,password,username) values("abc@123.com","abc123","lucas");