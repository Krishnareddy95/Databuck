/*
	26-Jan-2021 - Exception coming as one field (lengthCheckThreshold) got deleted (may be some incorrect script commit), field added again.
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholds' and lower(column_name) = 'lengthcheckthreshold') then	
		alter table listGlobalThresholds
			add column lengthCheckThreshold double not null default 0;
	end if;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

