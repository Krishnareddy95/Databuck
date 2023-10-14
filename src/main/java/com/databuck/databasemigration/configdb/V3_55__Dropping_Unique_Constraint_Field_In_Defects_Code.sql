/* Bug fix droping unique_constraint index */
delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	if exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'defect_codes' and lower(index_name) = 'unique_defect_code') then
		drop index unique_defect_code on defect_codes;
    end if;
    if exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'defect_codes' and lower(index_name) = 'unique_defect_dimension') then
    		drop index unique_defect_dimension on defect_codes;
        end if;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

