/*
 * Required params 1) skip numeric fields 2) number of parallel threads for default patterns and 3) disable flag for composite key. 
 */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='default.pattern.skip.numericfields') then	
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'default.pattern.skip.numericfields','N','Skip Numeric fields in default Pattern Check','N','N','N','','string','N',NOW());	
	end if;

    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='default.pattern.numthread') then	
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'default.pattern.numthread','1','Number of threads for default patterns','N','N','N','','string','N',NOW());	
	end if;
	
    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='composite.primarykey.disable.flag') then	
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'composite.primarykey.disable.flag','N','Disable identification of Composite key','N','N','N','','string','N',NOW());	
	end if;
	
	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;