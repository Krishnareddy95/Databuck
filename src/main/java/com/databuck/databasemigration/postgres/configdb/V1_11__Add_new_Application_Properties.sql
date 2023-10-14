
drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

begin
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='daylight.saving.enabled') then
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'daylight.saving.enabled','N','property to manage time difference','N','N','N','N','string','N',NOW());
	end if;

	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='profiling.num.thread') then
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'profiling.num.thread','30','property to manage thread count in template profiling','N','N','N','30','string','N',NOW());
	end if;

	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='integration') and property_name='slack.token') then
        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='integration'),'slack.token','','property to manage thread count in template profiling','N','N','N','','string','N',NOW());
    end if;
	
end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;