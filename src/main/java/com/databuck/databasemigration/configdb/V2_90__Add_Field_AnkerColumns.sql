drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'ankercolumns') then   
		alter table rule_Template_Mapping add column ankerColumns text;
   	end if;
   	
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'ankercolumns') then   
		alter table listColRules add column ankerColumns text;
   	end if;
   	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;