drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'filter_condition') then
		/* Add column */

		alter table listColGlobalRules drop column filter_condition;

		alter table listColGlobalRules add column filterId int(11);

	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'filterid') then

        alter table listColGlobalRules add column filterId int(11);

    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
