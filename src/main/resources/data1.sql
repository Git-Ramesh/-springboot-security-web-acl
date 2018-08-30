INSERT INTO `topic` (`ID`, `CATEGORY`, `TITLE`) VALUES ('1', 'Spring Rest Boot', 'Spring Boot');
INSERT INTO `topic` (`ID`, `CATEGORY`, `TITLE`) VALUES ('2', 'Spring Boot Security', 'Spring Boot');
INSERT INTO `topic` (`ID`, `CATEGORY`, `TITLE`) VALUES ('3', 'Spring MVC Framework', 'Spring Framework');


INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('ramesh', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'ramesh@gmail.com', 'ROLE_ADMIN', 'IN', '1');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('jaga', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'jaga@gmail.com', 'ROLE_USER', 'IN', '1');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('sowmya', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'sowmya@gmail.com', 'ROLE_ANONYMOUS', 'IN', '1');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('king', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'king@gmail.com', 'ROLE_USER', 'IN', '0');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('kiran', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'kiran@gmail.com', 'ROLE_VISITOR', 'IN', '1');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('rani', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'rani@gmail.com', 'ROLE_AUDITOR', 'IN', '1');
INSERT INTO `users` (`USERNAME`, `PASSWORD`, `EMAIL`, `ROLE`, `COUNTRY`, `ENABLED`) VALUES ('hari', '$2a$10$eQVP5obimcP5L1ZKFNoC.O3JzovIfy5I4Lz1ZkkDx1ikuBWnnpayK', 'hari@gmail.com', 'ROLE_ADMINSTRATION', 'IN', '1');

--If user or Principal the principal->true
	insert into acl_sid(id,principal,sid) values(1, 1, 'ramesh');
	insert into acl_sid(id,principal,sid) values(2, 1, 'jaga');
	insert into acl_sid(id,principal,sid) values(3, 0, 'ROLE_ADMIN');
	insert into acl_sid(id,principal,sid) values(4, 0, 'ROLE_USER');
	insert into acl_sid(id,principal,sid) values(5, 0, 'ROLE_VISITOR');
	insert into acl_sid(id,principal,sid) values(6, 0, 'ROLE_AUDITOR');
	insert into acl_sid(id,principal,sid) values(7, 0, 'ROLE_ADMINISTRATION');
	
insert into acl_class(id,class) values(1,'com.rs.app.model.Topic');

insert into acl_object_identity
(id,object_id_identity,object_id_class,parent_object,owner_sid,entries_inheriting) values 
	(1,1,1,null,1,false);
insert into acl_object_identity(id,object_id_identity,object_id_class,parent_object,owner_sid,entries_inheriting) values 
	(2,2,1,null,2,false);
insert into acl_object_identity(id,object_id_identity,object_id_class,parent_object,owner_sid,entries_inheriting) values (3,3,1,null,5,false);
	
insert into acl_entry(id,acl_object_identity,ace_order,sid,mask,granting,audit_success,audit_failure) values 
					  (1,       1,             0,       1,   1,  true,    true,         true);
insert into acl_entry(id,acl_object_identity,ace_order,sid,mask,granting,audit_success,audit_failure) values   (2,       2,             2,       2,   1,  true,    true,         true);
					  
insert into acl_entry(id,acl_object_identity,ace_order,sid,mask,granting,audit_success,audit_failure) values 
					  (3,       3,             3,       2,   16,  true,    true,         true);
					  
					  
					  
					  