/*
Navicat MySQL Data Transfer

Source Server         : dev
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : permsys

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2019-11-14 11:17:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `perm_id` int(11) NOT NULL AUTO_INCREMENT,
  `perm_name` varchar(255) NOT NULL COMMENT '权限名称，显示用',
  `perm_value` varchar(255) NOT NULL COMMENT '权限值',
  `perm_attachment` varchar(255) NOT NULL COMMENT '权限附加内容',
  PRIMARY KEY (`perm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', '涨薪', ':salary:raise', '/salary/raise');
INSERT INTO `permission` VALUES ('2', '降薪', ':salary:reduce', '/salary/reduce');
INSERT INTO `permission` VALUES ('3', '入职', ':hr:employ', '/hr/employ');
INSERT INTO `permission` VALUES ('4', '离职', ':hr:leave', '/hr/leave');
INSERT INTO `permission` VALUES ('5', '工会福利', ':lu:welfare', '/lu/welfare');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) DEFAULT NULL,
  `role_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '普通员工', 'staff');
INSERT INTO `role` VALUES ('2', '人事经理', 'hr_mgr');
INSERT INTO `role` VALUES ('3', '财务经理', 'finance_mgr');
INSERT INTO `role` VALUES ('4', '工会主席', 'lu_Presistent');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_perm_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `perm_id` int(11) NOT NULL,
  PRIMARY KEY (`role_perm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('1', '2', '1');
INSERT INTO `role_permission` VALUES ('2', '2', '2');
INSERT INTO `role_permission` VALUES ('3', '3', '3');
INSERT INTO `role_permission` VALUES ('4', '3', '4');
INSERT INTO `role_permission` VALUES ('5', '4', '5');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'abc@123.com', 'abc123', 'NORMAL');
INSERT INTO `user` VALUES ('2', 'cde@123.com', 'abc123', 'NORMAL');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_role_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '2');
INSERT INTO `user_role` VALUES ('2', '1', '4');
INSERT INTO `user_role` VALUES ('3', '2', '1');
