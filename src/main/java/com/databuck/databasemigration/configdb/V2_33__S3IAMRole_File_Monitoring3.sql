/* 
	Add idData field in file_tracking_history table to associate template Id to arrived file.
	Add fileExecutionStatus field in file_tracking_history table to identify if the file validation have been processed or not
	Add fileExecutionStatusMsg field in file_tracking_history table to mention the file execution message
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Add idData field in file_tracking_history table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'iddata') then	
		alter table file_tracking_history
			add column idData bigint(20) default null;
	end if;	
	
	/* Modify idData field in file_tracking_history table */
	if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'iddata') then	
		alter table file_tracking_history
			modify column idData bigint(20) default null;
	end if;	
	
	/* Add fileExecutionStatus field in file_tracking_history table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'fileexecutionstatus') then	
		alter table file_tracking_history
			add column fileExecutionStatus varchar(20);
	end if;	
	
	/* Change the fileExecutionStatus to 'processed' for existing data in table */
	start transaction;
	
	update file_tracking_history set fileExecutionStatus='processed';
	
	commit;
	
	/* Modify fileExecutionStatus field in file_tracking_history table */
	if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'fileexecutionstatus') then	
		alter table file_tracking_history
			modify column fileExecutionStatus varchar(20) not null default 'unprocessed';
	end if;	
	
	/* Add fileExecutionStatusMsg field in file_tracking_history table */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'fileexecutionstatusmsg') then	
		alter table file_tracking_history
			add column fileExecutionStatusMsg varchar(2000);
	end if;			
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
