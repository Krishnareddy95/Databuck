drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	/* Increase the size of rule name field */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'rulename') then

		alter table listColGlobalRules modify ruleName varchar(2500) default null;

	end if;
	
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'rule_template_mapping' and lower(column_name) = 'rulename') then

		alter table rule_Template_Mapping modify ruleName varchar(2500) default null;

	end if;

	/* Increase size of description field */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'description') then

		alter table listColGlobalRules modify description varchar(2500) default null;

	end if;
	
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'description') then

		alter table listColRules modify description varchar(2500) default null;

	end if;
	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;