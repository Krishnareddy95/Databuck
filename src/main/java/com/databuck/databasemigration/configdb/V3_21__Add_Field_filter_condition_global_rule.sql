drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'filter_condition') then
		/* Add column */
		alter table rule_Template_Mapping add column filter_condition varchar(2500) default '';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'filter_condition') then
        /* Add column */
        alter table listColGlobalRules add column filter_condition varchar(2500) default '';
    end if;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'filter_condition') then
        /* Add column */
        alter table listApplicationsRulesCatalog add column filter_condition varchar(2500) default '';
    end if;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'filter_condition') then
        /* Add column */
        alter table staging_listApplicationsRulesCatalog add column filter_condition varchar(2500) default '';
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
