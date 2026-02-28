create database product124;
use product124;
create table prd_sku
(
    sku_id      int auto_increment
        primary key,
    spu_id      int                                 not null comment '商品SPU ID',
    name        varchar(255)                        not null comment 'SKU名字',
    price       decimal(10, 2)                      not null comment 'SKU价格',
    img         varchar(255)                        not null comment 'SKU图片',
    description varchar(255)                        not null comment 'SKU介绍',
    category_id int                                 not null comment '分类ID',
    stock       int                                 not null comment '库存数量',
    status      tinyint                             not null comment '状态 0-下架 1-上架',
    create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品SKU表';



INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (1, 1, 'iphone17', 6000.00, 'https://example.com/img1.jpg', '这是商品1', 1, 89, 1, '2026-01-23 15:08:46', '2026-01-23 15:08:46');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (2, 2, 'ipad', 4000.00, 'https://example.com/img2.jpg', '这是商品2', 2, 200, 1, '2026-01-23 15:08:46', '2026-01-23 15:08:46');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (3, 3, 'macbook', 9000.00, 'https://example.com/img3.jpg', '这是商品3', 3, 300, 1, '2026-01-23 15:08:46', '2026-01-23 15:08:46');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (4, 4, '戴尔 XPS 13', 1299.99, 'dellxps13.jpg', '高性能轻薄本，适合办公', 2, 60, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (5, 5, '耐克 Air Max 270', 150.00, 'nikeairmax270.jpg', '舒适缓震跑鞋', 3, 300, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (6, 6, '阿迪达斯 Ultraboost 22', 180.00, 'adidasultraboost22.jpg', '经典跑鞋，回弹出色', 3, 250, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (7, 7, '联想 ThinkPad X1 Carbon', 1499.99, 'thinkpadx1carbon.jpg', '商务精英首选笔记本', 2, 50, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (8, 8, '华为 MateBook D14', 799.99, 'huaweimatebookd14.jpg', '性价比高的华为笔记本', 2, 120, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (9, 9, '小米 Redmi Note 12', 299.99, 'redminote12.jpg', '高性价比智能手机', 1, 400, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (10, 10, 'OPPO Reno 10 Pro', 499.99, 'opporeno10pro.jpg', '拍照功能强大的OPPO手机', 1, 350, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (11, 11, '索尼 WH-1000XM5 耳机', 399.99, 'sonywh1000xm5.jpg', '降噪效果出色的无线耳机', 4, 180, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (12, 12, 'Bose QuietComfort 45', 349.99, 'boseqc45.jpg', '舒适佩戴的降噪耳机', 4, 160, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (13, 13, '佳能 EOS R5 相机', 3899.99, 'canoneosr5.jpg', '专业全画幅无反相机', 5, 30, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (14, 14, '尼康 Z7 II', 2999.99, 'nikonz7ii.jpg', '高分辨率全画幅相机', 5, 25, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (15, 15, '任天堂 Switch OLED', 349.99, 'nintendoswitcholed.jpg', '便携式游戏主机', 6, 200, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (16, 16, 'PlayStation 5', 499.99, 'playstation5.jpg', '次世代游戏主机', 6, 100, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (17, 17, '宜家 Billy 书架', 79.99, 'ikeabilly.jpg', '经典简约书架', 7, 500, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (18, 18, '无印良品 棉质床单', 89.99, 'muji_bed_sheet.jpg', '舒适透气的纯棉床品', 7, 300, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (19, 19, '星巴克 星冰乐咖啡豆', 29.99, 'starbucks_frappuccino_beans.jpg', '香浓可口的咖啡豆', 8, 1000, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (20, 20, '雀巢 金牌咖啡胶囊', 49.99, 'nestle_gold_coffee_capsules.jpg', '方便快捷的咖啡胶囊', 8, 800, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (21, 1, '苹果 iPhone 15 Pro', 999.99, 'iphone15pro.jpg', '最新款苹果手机，性能强劲', 1, 150, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (22, 2, '三星 Galaxy S23', 899.99, 'galaxys23.jpg', '三星旗舰手机，拍照出色', 1, 200, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
INSERT INTO product124.prd_sku (sku_id, spu_id, name, price, img, description, category_id, stock, status, create_time, update_time) VALUES (23, 3, '苹果 MacBook Air M2', 1199.99, 'macbookairm2.jpg', '轻薄便携的苹果笔记本', 2, 80, 1, '2026-02-27 12:02:54', '2026-02-27 12:02:54');
