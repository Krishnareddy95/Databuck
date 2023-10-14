/* --------------- Modifying columns in schema_jobs_queue and schema_jobs_tracking -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'schema_jobs_queue' and lower(COLUMN_NAME) = 'uniqueId') then	
		alter table schema_jobs_queue
			MODIFY uniqueId varchar(2500);
	end if;

	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'schema_jobs_tracking' and lower(COLUMN_NAME) = 'uniqueId') then	
		alter table schema_jobs_tracking
			MODIFY uniqueId varchar(2500);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;