/* Add field validationRunType in runScheduledTasks - to detect unit_testing or full_load run  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	/* Add column validationRunType to table runScheduledTasks (Validation queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'runscheduledtasks' and lower(column_name) = 'validationruntype') then	
		alter table runScheduledTasks
			add column validationRunType varchar(100) default 'full_load';
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;