/* --------------- Adding columns for ListColRule - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listColRules' and lower(COLUMN_NAME) = 'ruleThreshold') then	
		alter table listColRules
			add column ruleThreshold double default 0 after sourcetemplatesecond;
	end if;

	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;