drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_sql_rules' and lower(column_name) = 'totalrecords') then
		/* Add column */
		ALTER table data_quality_sql_rules add column totalRecords bigint(20) ;
		update data_quality_sql_rules a set a.totalRecords=(select sum(b.RecordCount) from DATA_QUALITY_Transactionset_sum_A1 b where b.idApp=a.idapp and b.Date=a.date and b.Run=a.run);
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;