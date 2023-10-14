drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'custom_or_global_rule_type') then
		/* Add column */
		alter table listApplicationsRulesCatalog add column custom_or_global_rule_type varchar(2000) default null;

		/* Update default custom_or_global_rule_type for Custom rules */
		update listApplicationsRulesCatalog t1 set t1.custom_or_global_rule_type= (select case when (t2.ruleType is null) then '' else t2.ruleType end as ruleType  from listColRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Custom Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;

		/* Update default rule description for Global rules */
		update listApplicationsRulesCatalog t1 set t1.custom_or_global_rule_type= (select case when (t2.ruleType is null) then '' else t2.ruleType end as ruleType from listColGlobalRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Global Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;
	
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'custom_or_global_rule_type') then
		/* Add column */
		alter table staging_listApplicationsRulesCatalog add column custom_or_global_rule_type varchar(2000) default null;

		/* Update default custom_or_global_rule_type for Custom rules */
		update staging_listApplicationsRulesCatalog t1 set t1.custom_or_global_rule_type= (select case when (t2.ruleType is null) then '' else t2.ruleType end as ruleType  from listColRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Custom Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;

		/* Update default rule description for Global rules */
		update staging_listApplicationsRulesCatalog t1 set t1.custom_or_global_rule_type= (select case when (t2.ruleType is null) then '' else t2.ruleType end as ruleType from listColGlobalRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Global Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;

	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;