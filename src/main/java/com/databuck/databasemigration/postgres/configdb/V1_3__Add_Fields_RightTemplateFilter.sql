drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'right_template_filter_condition') then
		/* Add column */
		ALTER table rule_Template_Mapping add column right_template_filter_condition text default NULL;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'right_template_filter_id') then
		/* Add column */
		ALTER table listColGlobalRules add column right_template_filter_id int default NULL;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'right_template_filter_condition') then
		/* Add column */
		ALTER table listApplicationsRulesCatalog add column right_template_filter_condition text default NULL;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'right_template_filter_condition') then
		/* Add column */
		ALTER table staging_listApplicationsRulesCatalog add column right_template_filter_condition text default NULL;
	end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
