/* 
	11-Aug-2020 : Schema changes Rule catalog refinement / changes Cycle 02 (Wells Fargo)
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listApplicationsRulesCatalog' and lower(COLUMN_NAME) = 'activeFlag') then	
		alter table listApplicationsRulesCatalog
			add column activeFlag bit(1) not  null;
	end if;	
    
    end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* END */