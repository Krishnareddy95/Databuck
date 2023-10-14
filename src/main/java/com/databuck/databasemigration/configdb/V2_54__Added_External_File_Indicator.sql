/* Existing table 'listDataSchema' - Add columns for externalFile indicator   */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'externalfilenamepattern') then	
		alter table listDataSchema
			add column externalfileNamePattern varchar(10) default 'N';
	end if;	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'externalfilename') then	
		alter table listDataSchema
			add column externalfileName varchar(225);
	end if;
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'patterncolumn') then	
		alter table listDataSchema
			add column patternColumn varchar(225);
	end if;
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'headercolumn') then	
		alter table listDataSchema
			add column headerColumn varchar(225);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;