 /* Adding properties to support multiple cluster connections */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

    begin
                    if not exists (select 1 from databuck_properties_master where property_category_name='local') then
                        insert into databuck_properties_master(property_category_name,created_at) values ('local',now());
                    end if;

                    /* adding cluster category 'local' to databuck_properties_master */

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='deploymode') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='local'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='hive_mode') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='local'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hive support enabled.','Y','N','N','','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='numberOfPartitions') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='local'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='kerberos_enabled') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where  property_category_name='local'),'kerberos_enabled','N','Property to enable or disable kerberos','Y','N','N','','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='app_mode') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where  property_category_name='local'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='EMRCluster') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='hive_context_enabled') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where  property_category_name='local'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='deploymentMode') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='mapr.ticket.enabled') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='hdfs_result_directory') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='hdfs.accesslog.path') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
                    end if;

                    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='local') and property_name='hdfs_uri') then
                        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where   property_category_name='local'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
                    end if;

    end $$;

 call dummy_do_not_use();
 drop procedure if exists dummy_do_not_use;



