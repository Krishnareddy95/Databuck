drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	/* Add new property to integration category to enable Alation Integration */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='alation.integration.enabled') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'alation.integration.enabled','N','Property to enable Alation Integration','N','N','N','N','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Alation base url details */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='alation.integration.baseurl') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'alation.integration.baseurl','','Property to enter Alation BaseUrl details','N','N','N','','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Alation AccessToken */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='alation.integration.accesstoken') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'alation.integration.accesstoken','','Property to enter Alation AccessToken','N','Y','Y','','string','N',NOW());
	end if;
	
	/* Add new property to integration category to enter Alation RefreshToken */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='alation.integration.refreshtoken') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'alation.integration.refreshtoken','','Property to enter Alation RefreshToken','N','Y','Y','','string','N',NOW());
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;