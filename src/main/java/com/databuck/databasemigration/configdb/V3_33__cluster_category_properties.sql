/* Adding properties to support multiple cluster connections */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    	/* adding column cluster_property_category to listDataSchema */
    	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'cluster_property_category') then

        		alter table listDataSchema add column cluster_property_category varchar(255) default 'cluster';
        end if;

        /* inserting cluster category rows to databuck_properties_master */

        /* adding cluster category 'cdp' to databuck_properties_master */
        if not exists (select 1 from databuck_properties_master where property_category_name='cdp') then

        		insert into databuck_properties_master(property_category_name,created_at) values ('cdp',now());
        end if;

        /* inserting cluster category 'gcp' to databuck_properties_master */
        if not exists (select 1 from databuck_properties_master where property_category_name='gcp') then

        		insert into databuck_properties_master(property_category_name,created_at) values ('gcp',now());
        end if;

        /* inserting cluster category 'azure' to databuck_properties_master */
        if not exists (select 1 from databuck_properties_master where property_category_name='azure') then

        		insert into databuck_properties_master(property_category_name,created_at) values ('azure',now());
        end if;

        /* inserting cluster category 'mapr' to databuck_properties_master */
        if not exists (select 1 from databuck_properties_master where property_category_name='mapr') then

        		insert into databuck_properties_master(property_category_name,created_at) values ('mapr',now());
        end if;

        /* dropping duplicate constraints on databuck_property_details for column property_name */
        if exists(select 1 from information_schema.statistics WHERE table_schema = sSelectedDatabase AND lower(table_name) = 'databuck_property_details' and lower(column_name) = 'property_name') then

            ALTER TABLE `databuck_property_details` DROP INDEX `property_name`;

        end if;



        /* inserting to property categories */

        /* inserting cluster properties for category 'cluster' to databuck_property_details */

        if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cluster')) then

              insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cluster'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
              update databuck_property_details u1 set u1.property_value =(select u2.property_value from (SELECT * FROM databuck_property_details) u2 where u2.property_name='deploymentMode' and u2.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='appdb')) where u1.property_name='deploymentMode' and u1.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='cluster');
        end if;

        if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cluster')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cluster'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
               update databuck_property_details u1 set u1.property_value =(select u2.property_value from (SELECT * FROM databuck_property_details) u2 where u2.property_name='mapr.ticket.enabled' and u2.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='appdb')) where u1.property_name='mapr.ticket.enabled' and u1.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='cluster');
        end if;

        if not exists (select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cluster')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cluster'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
               update databuck_property_details u1 set u1.property_value =(select u2.property_value from (SELECT * FROM databuck_property_details) u2 where u2.property_name='hdfs_result_directory' and u2.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='appdb')) where u1.property_name='hdfs_result_directory' and u1.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='cluster');
        end if;

        if not exists (select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cluster')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cluster'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
               update databuck_property_details u1 set u1.property_value =(select u2.property_value from (SELECT * FROM databuck_property_details) u2 where u2.property_name='hdfs.accesslog.path' and u2.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='appdb')) where u1.property_name='hdfs.accesslog.path' and u1.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='cluster');
        end if;

        if not exists (select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cluster')) then

               insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cluster'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
               update databuck_property_details u1 set u1.property_value =(select u2.property_value from (SELECT * FROM databuck_property_details) u2 where u2.property_name='hdfs_uri' and u2.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='appdb')) where u1.property_name='hdfs_uri' and u1.property_category_id= (select d.property_category_id from databuck_properties_master d where d.property_category_name='cluster');
        end if;

         /* deleting from property category 'appdb' */

         /* delete row from databuck_property_details for property value 'deploymentMode' */
                if exists(select 1 from databuck_property_details  where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb') ) then

                    delete from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb');

                end if;
         /* delete row from databuck_property_details for property value 'mapr.ticket.enabled' */
                if exists(select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb')) then

                    delete from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb');

                end if;
         /* delete row from databuck_property_details for property value 'hdfs_result_directory' */
                if exists(select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb')) then

                    delete from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb');

                end if;
         /* delete row from databuck_property_details for property value 'hdfs.accesslog.path' */
                if exists(select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb')) then

                    delete from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb');

                end if;
         /* delete row from databuck_property_details for property value 'hdfs_uri' */
                if exists(select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb')) then

                    delete from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='appdb');

                end if;

        /* inserting cluster properties for category 'cdp' to databuck_property_details */

         if not exists (select 1 from databuck_property_details where property_name='deploymode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hive_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='numberOfPartitions' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='kerberos_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='app_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='EMRCluster' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hive_context_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
                end if;
         if not exists (select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='cdp')) then

                insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='cdp'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
                end if;


          /* inserting cluster properties for category 'gcp' to databuck_property_details */

          if not exists (select 1 from databuck_property_details where property_name='deploymode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='hive_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='numberOfPartitions' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='kerberos_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='app_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='EMRCluster' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='hive_context_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
                 end if;
          if not exists (select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='gcp')) then

                 insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='gcp'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
                 end if;

           /* inserting cluster properties for category 'azure' to databuck_property_details */

           if not exists (select 1 from databuck_property_details where property_name='deploymode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='hive_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='numberOfPartitions' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='kerberos_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='app_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='EMRCluster' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='hive_context_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
                  end if;
           if not exists (select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='azure')) then

                  insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='azure'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
                  end if;

         /* inserting cluster properties for category 'mapr' to databuck_property_details */

            if not exists (select 1 from databuck_property_details where property_name='deploymode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='hive_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='numberOfPartitions' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='kerberos_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='app_mode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='EMRCluster' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='hive_context_enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='deploymentMode' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='mapr.ticket.enabled' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='hdfs_result_directory' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='hdfs.accesslog.path' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N',NOW());
                   end if;
            if not exists (select 1 from databuck_property_details where property_name='hdfs_uri' and property_category_id= (select property_category_id from databuck_properties_master where property_category_name='mapr')) then

                   insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='mapr'),'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y',NOW());
                   end if;


    commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;