drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	if not exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'data_quality_data_drift_summary' and lower(index_name) = 'data_drift_summary_perf_index') then	
        create index data_drift_summary_perf_index on DATA_QUALITY_DATA_DRIFT_SUMMARY (idApp, Date);
    end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


 