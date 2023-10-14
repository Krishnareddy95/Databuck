/* Existing table 'listApplicationsRulesCatalog' - Add column agingCheckEnabled  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'agingcheckenabled') then	
		alter table listApplicationsRulesCatalog
			add column agingCheckEnabled varchar(100) default 'N';
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;