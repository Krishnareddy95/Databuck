drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    /* Add column */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_all' and lower(column_name) = 'dgroupval') then

        ALTER table DATA_QUALITY_Transaction_Detail_All add column dGroupVal varchar(3000);
	end if;
    

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_all' and lower(column_name) = 'dgroupcol') then

		 ALTER table DATA_QUALITY_Transaction_Detail_All add column dGroupCol varchar(3000);
	end if;


end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

