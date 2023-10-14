/* Existing table 'listDataSchema' - Add column local directory column in externalFile indicator   */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'localDirectoryColumnIndex') then	
		alter table listDataSchema
			add column localDirectoryColumnIndex varchar(100);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;