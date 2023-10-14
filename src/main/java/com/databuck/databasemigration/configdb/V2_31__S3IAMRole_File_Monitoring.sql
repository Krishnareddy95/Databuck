/* 
	Add idDataSchema field in fileMonitoring rules table to associate connection Id to rule
	Add partitionedFolders field in listDataSchema table to identify if partition folders are present or not
	Add partitionedFolders field in file_monitor_rules table to identify if partition folders are present or not
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Add idDataSchema field in fileMonitoring rules table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'partitionedfolders') then	
		alter table listDataSchema
			add column partitionedFolders varchar(10) default 'N';
	end if;	
	
	/* Add partitionedFolders field in listDataSchema table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_monitor_rules' and lower(column_name) = 'iddataschema') then	
		alter table file_monitor_rules
			add column idDataSchema bigint(20) default null;
	end if;	
	
	/* Modify partitionedFolders field in listDataSchema table */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_monitor_rules' and lower(column_name) = 'iddataschema') then	
		alter table file_monitor_rules
			modify column idDataSchema bigint(20) default null;
	end if;	
	
	/* Add partitionedFolders field in file_monitor_rules table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_monitor_rules' and lower(column_name) = 'partitionedfolders') then	
		alter table file_monitor_rules
			add column partitionedFolders varchar(10) not null default 'N';
	end if;	
	
	/* Modify partitionedFolders field in file_monitor_rules table */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_monitor_rules' and lower(column_name) = 'partitionedfolders') then	
		alter table file_monitor_rules
			modify column partitionedFolders varchar(10) not null default 'N';
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
