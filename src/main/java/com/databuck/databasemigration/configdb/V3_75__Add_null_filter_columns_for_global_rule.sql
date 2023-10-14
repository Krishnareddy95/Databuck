drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_Template_Mapping' and lower(column_name) = 'null_filter_columns') then
		    /* Add column */
		    alter table rule_Template_Mapping add column null_filter_columns text;
		    update rule_Template_Mapping set null_filter_columns=anchorColumns;
		end if;

		if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listApplicationsRulesCatalog' and lower(column_name) = 'null_filter_columns') then
            /* Add column */
            alter table listApplicationsRulesCatalog add column null_filter_columns text;
            update listApplicationsRulesCatalog set null_filter_columns=column_name;
        end if;

        if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listApplicationsRulesCatalog' and lower(column_name) = 'null_filter_columns') then
            /* Add column */
            alter table staging_listApplicationsRulesCatalog add column null_filter_columns text;
            update staging_listApplicationsRulesCatalog set null_filter_columns=column_name;
        end if;
    	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
