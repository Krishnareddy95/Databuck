/* Existing table 'listDataAccess' - Add column historicDateTable  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataaccess' and lower(column_name) = 'historicdatetable') then	
		alter table listDataAccess
			add column historicDateTable varchar(2000) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
