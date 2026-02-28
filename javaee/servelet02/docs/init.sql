create database  class126;
use class126;
create table wn_product(
                           id int primary key auto_increment,
                           name varchar(255),
                           price decimal(10,2),
                           description varchar(255),
                           image varchar(255)
);

insert into wn_product values
                           (null,'iphone',9999.99,'手机','https://picsum.photos/200/300'),

                           (null,'ipad',4999.99,'平板','https://picsum.photos/200/300'),
                           (null,'macbook',19999.99,'笔记本','https://picsum.photos/200/300'),
                           (null,'airpods',999.99,'无线蓝牙耳机','https://picsum.photos/200/300'),
                           (null,'watch',3999.99,'手表','https://picsum.photos/200/300');
select * from wn_product;


alter table wn_product
    change name title varchar(255) null;

alter table wn_product
    change image main_pic varchar(255) null;