-- ============================================================================
-- LangChain4j Agentic Demo —— 电商售后场景 数据库初始化脚本
-- 说明: 将原本 MockDataService 中的内存模拟数据迁移到 MySQL 真实数据库。
-- 执行方式(示例, 替换为你的实际账号密码):
--   mysql -u root -p < docs/init.sql
-- 或登录 MySQL 客户端后执行: source docs/init.sql
-- ============================================================================

-- 创建数据库(若不存在)
CREATE DATABASE IF NOT EXISTS `agentic_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `agentic_db`;

-- ----------------------------------------------------------------------------
-- 用户表
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id`     VARCHAR(32)  NOT NULL COMMENT '用户ID, 如 USR-001',
  `name`        VARCHAR(64)  NOT NULL COMMENT '用户姓名',
  `vip_level`   VARCHAR(16)  NOT NULL COMMENT 'VIP等级: NORMAL / VIP / SVIP',
  `order_count` INT          NOT NULL DEFAULT 0 COMMENT '历史订单数',
  `refund_count` INT         NOT NULL DEFAULT 0 COMMENT '历史退款次数',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------------------------------------------------------
-- 商品表
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` VARCHAR(32)  NOT NULL COMMENT '商品ID, 如 PRD-001',
  `name`       VARCHAR(128) NOT NULL COMMENT '商品名称',
  `category`   VARCHAR(64)  DEFAULT NULL COMMENT '商品分类',
  `price`      DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `stock`      INT          NOT NULL DEFAULT 0 COMMENT '库存',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------------------------------------------------------
-- 订单表 (order 为 MySQL 保留字, 使用反引号包裹)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_id`    VARCHAR(32) NOT NULL COMMENT '订单ID, 如 ORD-001',
  `user_id`     VARCHAR(32) NOT NULL COMMENT '下单用户ID',
  `status`      VARCHAR(16) NOT NULL COMMENT '订单状态: PAID / SHIPPED / DELIVERED / CANCELLED',
  `total_price` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '订单总价',
  PRIMARY KEY (`order_id`),
  KEY `idx_order_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------------------------------------------------------
-- 订单项表 (一个订单可包含多个商品, 一对多)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id`    VARCHAR(32)  NOT NULL COMMENT '所属订单ID',
  `product_id`  VARCHAR(32)  NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称(下单时快照)',
  `quantity`    INT          NOT NULL DEFAULT 1 COMMENT '购买数量',
  `unit_price`  DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '下单时单价',
  PRIMARY KEY (`id`),
  KEY `idx_item_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================================================
-- 示例数据 (与改造前的 MockDataService 保持一致)
-- ============================================================================

-- 用户
INSERT INTO `user` (`user_id`, `name`, `vip_level`, `order_count`, `refund_count`) VALUES
  ('USR-001', '张三', 'NORMAL', 5, 0),
  ('USR-002', '李四', 'VIP',    15, 1),
  ('USR-003', '王五', 'NORMAL', 2, 4);

-- 商品
INSERT INTO `product` (`product_id`, `name`, `category`, `price`, `stock`) VALUES
  ('PRD-001', '蓝牙耳机', '数码', 299.00, 50),
  ('PRD-002', '机械键盘', '数码', 599.00, 20),
  ('PRD-003', '运动跑鞋', '运动', 459.00, 100);

-- 订单
INSERT INTO `order` (`order_id`, `user_id`, `status`, `total_price`) VALUES
  ('ORD-001', 'USR-001', 'DELIVERED', 299.00),
  ('ORD-002', 'USR-002', 'SHIPPED',   599.00),
  ('ORD-003', 'USR-003', 'PAID',      918.00);

-- 订单项
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `quantity`, `unit_price`) VALUES
  ('ORD-001', 'PRD-001', '蓝牙耳机', 1, 299.00),
  ('ORD-002', 'PRD-002', '机械键盘', 1, 599.00),
  ('ORD-003', 'PRD-003', '运动跑鞋', 2, 459.00);

-- ----------------------------------------------------------------------------
-- 人工审批(HumanInTheLoop)请求表
-- 取代原本内存中的 Map<String, Future<String>> 易失状态，作为「暂停→等待人工→恢复」
-- 唯一的状态真相来源(source of truth)。进程重启后可恢复、可审计、可查询。
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `approval_request`;
CREATE TABLE `approval_request` (
  `request_id`       VARCHAR(40)    NOT NULL COMMENT '审批请求ID, 如 REQ-xxxxxxxx',
  `order_id`         VARCHAR(32)    NOT NULL COMMENT '关联订单ID',
  `reason`           VARCHAR(512)   DEFAULT NULL COMMENT '退款原因',
  `amount`           DECIMAL(12,2)  NOT NULL DEFAULT 0.00 COMMENT '申请退款金额',
  `precheck_result`  MEDIUMTEXT     COMMENT '前置检查Agent产出材料(供人工审批参考)',
  `status`           VARCHAR(32)    NOT NULL COMMENT '状态机: PENDING_PRECHECK / AWAITING_APPROVAL / EXECUTING / EXECUTED / REJECTED / FAILED',
  `decision`         VARCHAR(16)    DEFAULT NULL COMMENT '审批结论: APPROVED / REJECTED',
  `decision_comment` VARCHAR(512)   DEFAULT NULL COMMENT '审批备注',
  `approver`         VARCHAR(64)    DEFAULT NULL COMMENT '审批人',
  `execution_result` MEDIUMTEXT     COMMENT '执行Agent产出结果',
  `error_message`    VARCHAR(1024)  DEFAULT NULL COMMENT '失败原因(status=FAILED时)',
  `created_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `completed_at`     DATETIME       DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`request_id`),
  KEY `idx_status` (`status`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工审批(HumanInTheLoop)请求表';
