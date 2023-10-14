drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_identity' and lower(column_name) = 'dgroupcol') then
		/* Add column */
		ALTER table DATA_QUALITY_Transaction_Detail_Identity add column dGroupCol text ;
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_identity' and lower(column_name) = 'dgroupval') then
        /* Add column */
        ALTER table DATA_QUALITY_Transaction_Detail_Identity add column dGroupVal text ;
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;