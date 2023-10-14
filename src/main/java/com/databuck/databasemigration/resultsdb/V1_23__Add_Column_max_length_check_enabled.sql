/* 
	Adding New Column max_length_check_enabled inside DATA_QUALITY_Length_Check.
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_length_check' and lower(column_name) = 'max_length_check_enabled') then
		alter table DATA_QUALITY_Length_Check
			add column max_length_check_enabled varchar(10) default 'N';
	end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;