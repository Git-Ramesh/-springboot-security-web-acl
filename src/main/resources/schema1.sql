drop table if exists topic;
drop table if exists users;
drop table if exists acl_sid;
drop table if exists acl_class;
drop table if exists acl_object_identity; 
drop table if exists acl_entry;
drop table if exists hibernate_sequence;
create table hibernate_sequence (
       next_val bigint
    ) engine=MyISAM;
insert into hibernate_sequence values ( 1 );
--drop sequence if exists hibernate_sequence;
--create sequence hibernate_sequence start with 1 increment by 1;
create table topic (
       id integer not null auto_increment,
        category varchar(25),
        title varchar(25),
        primary key (id)
    );
create table users (
       username varchar(50) not null,
        country varchar(30),
        email varchar(38) not null,
        enabled integer,
        password varchar(100) not null,
        role varchar(38) not null,
        primary key (username)
    );
alter table users 
       add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
       

create table acl_sid(
	id bigint  not null auto_increment primary key,
	principal boolean not null,
	sid varchar(100) not null,
	constraint unique_uk_1 unique(sid, principal)
);

create table acl_class(
	id bigint not null auto_increment primary key,
	class varchar(100) not null,
	constraint unique_uk_2 unique(class)
);

create table acl_object_identity(
	id bigint not null auto_increment primary key,
	object_id_class bigint not null,
	object_id_identity bigint not null,
	parent_object bigint,
	owner_sid bigint,
	entries_inheriting boolean not null,
	constraint unique_uk_3 unique(object_id_class,object_id_identity),
	constraint foreign_fk_1 foreign key(parent_object) references acl_object_identity(id) ON DELETE CASCADE
);

create table acl_entry (
	id bigint not null auto_increment primary key,
	acl_object_identity bigint not null,
	ace_order int not null,
	sid bigint not null,
	mask integer not null,
	granting boolean not null,
	audit_success boolean not null,
	audit_failure boolean not null,
	constraint unique_uk_4 unique(acl_object_identity,ace_order),
	constraint foreign_fk_4 foreign key(acl_object_identity) references acl_object_identity(id) ON DELETE CASCADE,
	constraint foreign_fk_5 foreign key(sid) references acl_sid(id) ON DELETE CASCADE
);







