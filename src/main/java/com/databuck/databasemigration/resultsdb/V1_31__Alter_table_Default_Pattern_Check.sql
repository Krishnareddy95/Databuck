/* --------------- Adding columns for DATA_QUALITY_Unmatched_Default_Pattern_Data - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_unmatched_default_pattern_data' and lower(COLUMN_NAME) = 'csv_file_path') then	
		alter table DATA_QUALITY_Unmatched_Default_Pattern_Data add column Csv_File_Path text;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;