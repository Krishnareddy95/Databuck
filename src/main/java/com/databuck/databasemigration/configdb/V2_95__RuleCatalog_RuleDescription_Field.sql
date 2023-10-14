drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'rule_description') then   
		/* Add column */
		alter table listApplicationsRulesCatalog add column rule_description text default null; 
		
		/* Update default rule description for Auto discovered and global rules */
		update listApplicationsRulesCatalog t1 set t1.rule_description= t1.rule_type where t1.rule_type = 'Global Rule' or t1.rule_category='Auto Discovery';
		
		/* Update default rule description for Custom rules */
		update listApplicationsRulesCatalog t1 set t1.rule_description= (select case when (t2.description is null) then '' else t2.description end as description  from listColRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Custom Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplicationsrulescatalog' and lower(column_name) = 'rule_description') then   
		/* Add column */
		alter table staging_listApplicationsRulesCatalog add column rule_description text default null;   
		
		/* Update default rule description for Auto discovered and global rules */
		update staging_listApplicationsRulesCatalog t1 set t1.rule_description= t1.rule_type where t1.rule_type = 'Global Rule' or t1.rule_category='Auto Discovery';
		
		/* Update default rule description for Custom rules */
		update staging_listApplicationsRulesCatalog t1 set t1.rule_description= (select case when (t2.description is null) then '' else t2.description end as description from listColRules t2 where t2.idListColrules=t1.custom_or_global_ruleId) where t1.rule_type='Custom Rule' and t1.custom_or_global_ruleId is not null and t1.custom_or_global_ruleId > 0;

	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;