drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	start transaction;
	
	
	if exists (select 1 from staging_listApplicationsRulesCatalog where rule_type ='Record Anomaly Check') then
		UPDATE staging_listApplicationsRulesCatalog set rule_type ='Value Anomaly Check' where rule_type ='Record Anomaly Check';
	end if;
	
	if exists (select 1 from listApplicationsRulesCatalog where rule_type ='Record Anomaly Check') then
		UPDATE listApplicationsRulesCatalog set rule_type ='Value Anomaly Check' where rule_type ='Record Anomaly Check';
	end if;
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;