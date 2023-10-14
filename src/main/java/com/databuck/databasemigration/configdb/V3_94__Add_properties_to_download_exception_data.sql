drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='hdfs_user') then
		insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at)
		values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'hdfs_user','','To execute hdfs cli commands its needed','N','N','N','','string','N',NOW());
	end if;

	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='storage_account_name') then
        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at)
        values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'storage_account_name','','To save download exception data for azure, its needed','N','N','N','','string','N',NOW());
    end if;

    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='storage_account_key') then
        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at)
        values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'storage_account_key','','To save download exception data for azure, its needed','N','N','N','','string','N',NOW());
    end if;

    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='exception_data_download_limit') then
        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at)
        values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'exception_data_download_limit','100','To execute hdfs cli commands its needed','N','N','N','100','int','N',NOW());
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;