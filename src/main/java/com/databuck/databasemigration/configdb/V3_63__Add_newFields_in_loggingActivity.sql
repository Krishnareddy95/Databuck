drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'logging_activity' and lower(column_name) = 'entity_id') then
		/* Add column */
		alter table logging_activity add column entity_id int;
		end if;

		if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'logging_activity' and lower(column_name) = 'entity_name') then
                        /* Add column */
        alter table logging_activity add column entity_name varchar(255) default 'N';
        end if;

		if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'logging_activity' and lower(column_name) = 'activity_name') then
        /* Add column */
		alter table logging_activity add column activity_name varchar(255) default 'N';
    	end if;
    	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
