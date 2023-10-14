/* 
	01-July-2020 : Schema changes Rule catalog refinement / changes Cycle 01 (Wells Fargo)
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Rules catalog changes - rule name needed apart from column name for non column based features */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'rule_name') then	
		alter table listApplicationsRulesCatalog
			add column rule_name varchar(255) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
