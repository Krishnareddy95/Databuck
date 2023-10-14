drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'matchingrules') then
		/* Add column */
		alter table rule_Template_Mapping add column matchingRules varchar(2500) default '';

		update rule_Template_Mapping set matchingRules=ruleExpression where lower(ruleType)='orphan';

        update rule_Template_Mapping set ruleExpression= '' where lower(ruleType)='orphan';

	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
