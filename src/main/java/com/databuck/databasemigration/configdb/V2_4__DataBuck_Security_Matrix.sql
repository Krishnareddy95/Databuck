/* To act as generic table for databuck security control and data filering */
create table if not exists databuck_security_matrix (
   row_id                  int(11) not null primary key auto_increment,
   ldap_group_name 	      varchar(255),
  	idRole				      bigint(20),   
   unique key databuck_security_matrix (ldap_group_name, idRole)      
) engine=innodb auto_increment=1 default charset=latin1;

/* Bug fix duplicate Role name can be entered, so added unique index on roleName */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'role' and lower(index_name) = 'rolename') then	
		create unique index roleName on Role (roleName);
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;



