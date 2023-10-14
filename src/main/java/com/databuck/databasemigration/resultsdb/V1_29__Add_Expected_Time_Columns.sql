drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_filearrival_details' and lower(column_name) = 'expected_hour') then
		/* Add column */
		ALTER table dbk_fm_filearrival_details add column expected_hour smallint;
	end if;


	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_filearrival_details' and lower(column_name) = 'expected_time') then
			/* Add column */
		ALTER table dbk_fm_filearrival_details add column expected_time smallint;
		end if;

commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;