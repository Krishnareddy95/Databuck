drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'reprofiling') then
		/* Add column */
		alter table listApplications add column reprofiling varchar(10) default 'N';
	end if;
    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplications' and lower(column_name) = 'reprofiling') then
    		/* Add column */
    		alter table staging_listApplications add column reprofiling varchar(10) default 'N';
    	end if;
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;