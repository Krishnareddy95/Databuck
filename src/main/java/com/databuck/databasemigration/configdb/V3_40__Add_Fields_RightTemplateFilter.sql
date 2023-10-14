drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'right_template_filter_condition') then
		/* Add column */
		alter table rule_Template_Mapping add column right_template_filter_condition text default NULL;
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'right_template_filter_id') then
        /* Add column */
         alter table listColGlobalRules add column right_template_filter_id int(11) default null;
    end if;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'right_template_filter_condition') then
        /* Add column */
        alter table listApplicationsRulesCatalog  add column right_template_filter_condition text default NULL;
    end if;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'right_template_filter_condition') then
        /* Add column */
        alter table staging_listApplicationsRulesCatalog  add column right_template_filter_condition text default NULL;
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
