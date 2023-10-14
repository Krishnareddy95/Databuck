drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_record_anomaly' and lower(column_name) = 'threshold') then
        /* Add column */
        ALTER table DATA_QUALITY_Record_Anomaly add column threshold double default null ;
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;