-- -------------------------------------------------------------
-- TablePlus 3.1.2(296)
--
-- https://tableplus.com/
--
-- Database: seckill
-- Generation Time: 2020-03-04 09:54:14.5300
-- -------------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP TABLE IF EXISTS `sk_order`;
CREATE TABLE `sk_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `product_id` int(11) NOT NULL COMMENT '抢购到的商品',
  `seckill_id` int(11) NOT NULL COMMENT '关联的秒杀活动',
  `amount` int(11) NOT NULL COMMENT '数量',
  `order_status` varchar(32) DEFAULT NULL COMMENT '订单状态  CREATE | PAID | SENT | FINISH',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `sent_time` datetime  NULL COMMENT '发货时间',
  `finish_time` datetime  NULL COMMENT '结束时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 唯一索引保证不重复
create UNIQUE index uk_seckill_userid_product_id on sk_order(user_id,product_id);

DROP TABLE IF EXISTS `sk_product`;
CREATE TABLE `sk_product` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `seckill_id` int(11) NOT NULL COMMENT '秒杀ID',
  `main_img` varchar(1024) NOT NULL COMMENT '主图',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称',
  `suggest_price` decimal(17,2) NOT NULL COMMENT '建议价格',
  `price` decimal(17,2) NOT NULL COMMENT '价格',
  `model` varchar(1024) NOT NULL COMMENT '模型，json格式保存',
  `stock` int(11) NOT NULL COMMENT '存货量',
  `description` varchar(1024) NOT NULL COMMENT '介绍',
  `sales_count` int(11) NOT NULL COMMENT '销量',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='商品';

DROP TABLE IF EXISTS `sk_seckill`;
CREATE TABLE `sk_seckill` (
  `seckill_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `seckill_status` varchar(20) NOT NULL COMMENT '秒杀状态, INIT | PROCESSING | END',
  PRIMARY KEY (`seckill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `sk_product` (`product_id`, `seckill_id`, `main_img`, `product_name`, `suggest_price`, `price`, `model`, `stock`, `description`, `sales_count`) VALUES
('1', '1', 'https://img14.360buyimg.com/mobilecms/s300x300_jfs/t6745/282/626763773/78968/4d4a491c/59427636N141dff87.jpg!q70.jpg', '婴幼儿奶粉', '200.00', '170.50', '{[\"3岁\",\"4岁\"]}', '10', '学习下', '1000'),
('2', '1', 'https://img11.360buyimg.com/mobilecms/s300x300_jfs/t1/25703/10/9775/160632/5c7fbe08E2840b107/1e0dde0aa554340f.jpg!q70.jpg', '辅食', '200.00', '121.00', '{[\"3岁\",\"4岁\"]}', '1000', '学习下', '2222');

INSERT INTO `sk_seckill` (`seckill_id`, `start_time`, `end_time`, `seckill_status`) VALUES
('1', '2020-03-03 17:16:02', '2020-03-03 14:16:02', 'PROCESSING');




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;