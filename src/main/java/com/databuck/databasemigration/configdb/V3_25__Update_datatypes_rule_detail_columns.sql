drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	/* Update datatypes in listColRules table */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'matchingrules') then
		alter table listColRules modify column matchingRules text;
	end if;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'expression') then
		alter table listColRules modify column expression text;
    end if;
    
    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'description') then
		alter table listColRules modify column description varchar(500);
    end if;
    
    /* Update datatypes in listColGlobalRules table */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'matchingrules') then
		alter table listColGlobalRules modify column matchingRules text;
	end if;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'expression') then
		alter table listColGlobalRules modify column expression text;
    end if;
    
    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'description') then
		alter table listColGlobalRules modify column description varchar(500);
    end if;
    
    /* Update datatypes in rule_Template_Mapping table */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'matchingrules') then
		alter table rule_Template_Mapping modify column matchingRules text;
	end if;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'ruleexpression') then
		alter table rule_Template_Mapping modify column ruleExpression text;
    end if;
    
    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'filter_condition') then
		alter table rule_Template_Mapping modify column filter_condition text;
    end if;
    
    /* Update datatypes in listApplicationsRulesCatalog table */
    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'filter_condition') then
		alter table listApplicationsRulesCatalog modify column filter_condition text;
    end if;
    
     /* Update datatypes in staging_listApplicationsRulesCatalog table */    
    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'filter_condition') then
		alter table staging_listApplicationsRulesCatalog modify column filter_condition text;
    end if;
      
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
