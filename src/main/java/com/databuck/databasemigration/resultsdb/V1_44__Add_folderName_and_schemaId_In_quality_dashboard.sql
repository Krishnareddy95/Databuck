/* 
	Adding New Column idDataSchema and folderName in data_quality_dashboard table
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_dashboard' and lower(column_name) = 'iddataschema') then
		alter table data_quality_dashboard
			add column idDataSchema bigint(20) default NULL;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_dashboard' and lower(column_name) = 'foldername') then
		alter table data_quality_dashboard
			add column folderName varchar(50) default NULL;
	end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;