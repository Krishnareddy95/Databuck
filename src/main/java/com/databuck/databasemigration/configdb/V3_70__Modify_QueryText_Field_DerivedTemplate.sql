drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listderiveddatasources' and lower(column_name) = 'querytext') then
			/* Modify column */
			alter table listDerivedDataSources modify column queryText text;
		end if;
    	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
