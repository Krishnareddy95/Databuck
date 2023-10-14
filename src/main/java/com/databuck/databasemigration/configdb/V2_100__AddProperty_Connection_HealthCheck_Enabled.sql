drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	/* Add new property to appdb category to perform health check on connection view */
	if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='connection.healthcheck.enabled') then
	    insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'connection.healthcheck.enabled','N','Property to perform health check of connection','N','N','N','N','string','N',NOW());
	end if;

    /* Add column  healthCheck to make connection enable for healthCheck job*/
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'healthcheck') then
    	alter table schema_jobs_queue add column healthCheck varchar(10) default 'N';
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;