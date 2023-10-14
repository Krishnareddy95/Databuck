/* Adding properties to support multiple cluster connections */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	
	 /* adding cluster category 'databricks' to databuck_properties_master */
        if not exists (select 1 from databuck_properties_master where property_category_name='databricks') then

        		insert into databuck_properties_master(property_category_name,created_at) values ('databricks',now());
        end if;
		
		
		if not exists (select 1 from databuck_property_details where property_name='deploymode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hive_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='numberOfPartitions' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='kerberos_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='app_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='EMRCluster' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hive_context_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='adls.azure.storageName' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'adls.azure.storageName','','provide azure data lake storageName','N','N','N','','string','N',NOW());
	            end if;
         if not exists (select 1 from databuck_property_details where property_name='adls.azure.containerName' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'adls.azure.containerName','','provide azure data lake containerName','N','N','N','','string','N',NOW());
	           end if;
         if not exists (select 1 from databuck_property_details where property_name='adls.azure.accessKey' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'adls.azure.accessKey','','provide azure data lake accessKey','N','N','N','','string','N',NOW());
	           end if;
         if not exists (select 1 from databuck_property_details where property_name='adls.azure.propertiesfilePath' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='databricks')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='databricks'),'adls.azure.propertiesfilePath','','provide azure data lake propertiesfilePath','N','N','N','','string','N',NOW());
	           end if;
				
				
				
 commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;				
