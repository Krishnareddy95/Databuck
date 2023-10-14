drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'user_token' and lower(column_name) = 'refreshtoken') then
		/* Add column */
		alter table user_token add column refreshtoken varchar(2000);
		end if;
    	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
