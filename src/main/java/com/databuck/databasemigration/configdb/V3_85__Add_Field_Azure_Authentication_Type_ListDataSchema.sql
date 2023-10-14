drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listDataSchema' and lower(column_name) = 'azure_authentication_type') then
		/* Add column */
		alter table listDataSchema add column azure_authentication_type varchar(100);
		end if;
    	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
