-- oracle
drop table tjs_user;
create table tjs_user(
user_id number(10) PRIMARY KEY ,
username varchar2(128) not null,
password VARCHAR2(128) not null,
age NUMBER(10) ,
birthday TIMESTAMP
);

insert into tjs_user values(1,'lucas','123456',12,sysdate);
commit;
select * from tjs_user;

drop table blog;
create table blog(
  blog_id NUMBER(10) PRIMARY KEY ,
  title VARCHAR2(256) NOT NULL,
  state VARCHAR2(6) NOT NULL ,
  author_name VARCHAR2(256) NOT NULL ,
  featured NUMBER(1) not null
);

insert into blog values(1,'如何写出结构良好的代码','ACTIVE','Lucas',1);
insert into blog values(2,'如何读懂糟糕的代码','ACTIVE','Phli',2);
insert into blog values(3,'如何快速编码，又名5分钟写出一个项目','INACTIVE','Trump',1);
commit;