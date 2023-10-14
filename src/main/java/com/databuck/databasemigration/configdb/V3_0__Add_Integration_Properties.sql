drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	/* Add new property category */
	if not exists (select 1 from databuck_properties_master where property_category_name='integration') then
		insert into databuck_properties_master(property_category_name,created_at) values ('integration',now());
	end if;
	
	/* Add new property to integration category to enable Jira Integration */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.integration.enabled') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.integration.enabled','N','Property to enable health Jira Integration','N','N','N','N','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Jira host:port details */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.api.hostport') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.api.hostport','','Property to enter Jira host:port details','N','N','N','','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Jira Username */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.api.username') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.api.username','','Property to enter Jira username','N','N','N','','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Jira APIToken */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.api.apitoken') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.api.apitoken','','Property to enter Jira APIToken','N','Y','Y','','string','N',NOW());
	end if;

	/* Add new property to integration category to enter Jira ProjectKey */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.api.projectkey') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.api.projectkey','','Property to enter ProjectKey','N','N','N','','string','N',NOW());
	end if;
	
	/* Add new property to integration category to enter Jira Min acceptable DQI */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='jira.api.app.min.dqi') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'jira.api.app.min.dqi','100.0','Property to enter min acceptable DQI, below which it is failure','N','N','N','100.0','double','N',NOW());
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;