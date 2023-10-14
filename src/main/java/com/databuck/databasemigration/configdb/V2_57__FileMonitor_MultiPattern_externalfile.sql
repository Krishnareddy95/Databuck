/* Existing table 'schema_multipattern_info' - Add column subFolderName  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_multipattern_info' and lower(column_name) = 'subfoldername') then	
		alter table schema_multipattern_info
			add column subFolderName varchar(100) default null;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;