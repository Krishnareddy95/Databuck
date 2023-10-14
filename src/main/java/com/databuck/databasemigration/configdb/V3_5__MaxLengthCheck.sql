/* 
	26-Nov-2021 - Max Length check feature changes to confige DB.	
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdatadefinition' and lower(column_name) = 'maxlengthcheck') then	
		alter table listDataDefinition
			add column maxLengthCheck varchar(1) default null;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listdatadefinition' and lower(column_name) = 'maxlengthcheck') then	
		alter table staging_listDataDefinition
			add column maxLengthCheck varchar(1) default null;
	end if;	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'maxlengthcheck') then	
		alter table listApplications
			add column maxLengthCheck varchar(1) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

