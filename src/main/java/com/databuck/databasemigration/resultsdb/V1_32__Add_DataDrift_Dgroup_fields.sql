drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_data_drift_count_summary' and lower(column_name) = 'dgroupcol') then
		/* Add column */
		ALTER table DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY add column dGroupCol varchar(3000);
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_data_drift_count_summary' and lower(column_name) = 'dgroupval') then
		/* Add column */
		ALTER table DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY add column dGroupVal varchar(3000);
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;