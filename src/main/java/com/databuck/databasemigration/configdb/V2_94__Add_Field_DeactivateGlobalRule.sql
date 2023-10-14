drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'activeflag') then   
		alter table rule_Template_Mapping add column activeFlag varchar(10) default 'Y';
		
		update listApplicationsRulesCatalog t1 set t1.custom_or_global_ruleId = (select t2.ruleId from rule_Template_Mapping t2 where t1.custom_or_global_ruleId=t2.id) where t1.rule_type='Global Rule';
		
		update staging_listApplicationsRulesCatalog t1 set t1.custom_or_global_ruleId = (select t2.ruleId from rule_Template_Mapping t2 where t1.custom_or_global_ruleId=t2.id) where t1.rule_type='Global Rule';
   	
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;