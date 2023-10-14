drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'columncountcheck') then
		/* Add column */
		alter table file_tracking_history add column columnCountCheck varchar(255) default 'null';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'columnsequencecheck') then
		/* Add column */
		alter table file_tracking_history add column columnSequenceCheck varchar(255) default 'null';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'fileformat') then
		/* Add column */
		alter table file_tracking_history add column fileFormat varchar(255) default 'null';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'recordlengthcheck') then
		/* Add column */
		alter table file_tracking_history add column recordLengthCheck varchar(255) default 'null';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'zerosizefilecheck') then
		/* Add column */
		alter table file_tracking_history add column zeroSizeFileCheck varchar(255) default 'null';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'file_tracking_history' and lower(column_name) = 'recordmaxlengthcheck') then
		/* Add column */
		alter table file_tracking_history add column recordMaxLengthCheck varchar(255) default 'null';
	end if;


	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;