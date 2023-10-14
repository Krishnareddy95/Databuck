/* 
	09-Oct-2020 : Project Id is absent in custom column rules, so cannot filter on project membership basis
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Rules catalog changes - rule name needed apart from column name for non column based features */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'project_id') then	
		alter table listColRules
			add column project_id int(11) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
