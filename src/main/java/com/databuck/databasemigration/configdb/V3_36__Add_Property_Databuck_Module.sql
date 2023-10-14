drop procedure if exists dummy_do_not_use;
delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	/* Add new property Databuck Module With Default Values */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='databuck.module') then
    	insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) 
		values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'databuck.module','all','Property to mention the name of module','N','N','N','all','string','N',NOW());
    end if;

	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;