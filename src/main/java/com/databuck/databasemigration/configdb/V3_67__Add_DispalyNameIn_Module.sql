drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	start transaction;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'module' and lower(column_name) = 'displayname') then	
		alter table Module
			add column displayName VARCHAR(500);
	end if;
	
	UPDATE Module set displayName = taskName ; 

	UPDATE Module set displayName ='Validation Templates' where taskName ='Data Template';

	UPDATE Module set displayName ='Custom Rules' where taskName ='Global Rule';

	UPDATE Module set displayName ='Rule Catalog' where taskName ='Validation Check';

	UPDATE Module set displayName ='Quick Start' where taskName ='QuickStart';
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;