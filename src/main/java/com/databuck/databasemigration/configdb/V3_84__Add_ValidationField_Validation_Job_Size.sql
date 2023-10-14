drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'validation_job_size') then
		/* Add column */
		alter table listApplications add column validation_job_size varchar(20) default 'medium';
	end if;
    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplications' and lower(column_name) = 'validation_job_size') then
    		/* Add column */
    		alter table staging_listApplications add column validation_job_size varchar(20) default 'medium';
    	end if;
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;