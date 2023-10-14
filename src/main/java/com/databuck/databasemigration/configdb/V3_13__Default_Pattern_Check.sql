/*
 * Required fields and params for adding default patterns to template and applications
 */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='default.pattern.threshold') then	
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'default.pattern.threshold','10','Threshold for default Pattern Check','N','N','N','','string','N',NOW());	
	end if;

    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='default.pattern.top3.threshold') then	
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'default.pattern.top3.threshold','90','Threshold for Top three default patterns','N','N','N','','string','N',NOW());	
	end if;
	
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listDataDefinition' and lower(column_name) = 'defaultPatternCheck') then   
		alter table listDataDefinition add column `defaultPatternCheck`  varchar(10) NOT NULL DEFAULT 'N';	
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listDataDefinition' and lower(column_name) = 'defaultPatternCheck') then   
		alter table staging_listDataDefinition add column `defaultPatternCheck`  varchar(10) NOT NULL DEFAULT 'N';	
	end if;

   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listDataDefinition' and lower(column_name) = 'defaultPatterns') then   
		alter table listDataDefinition add column  `defaultPatterns` text DEFAULT null;	
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listDataDefinition' and lower(column_name) = 'defaultPatterns') then   
		alter table staging_listDataDefinition add column  `defaultPatterns` text DEFAULT null;	
	
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listApplications' and lower(column_name) = 'defaultPatternCheck') then   
		alter table listApplications add column defaultPatternCheck varchar(10) not null default 'N';
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listApplications' and lower(column_name) = 'defaultPatternCheck') then   
		alter table staging_listApplications add column defaultPatternCheck varchar(10) not null default 'N';
	end if;
	
	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;