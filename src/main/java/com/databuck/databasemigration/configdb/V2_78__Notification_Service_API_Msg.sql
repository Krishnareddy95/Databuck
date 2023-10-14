
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
    declare sSelectedDatabase varchar(100) default '';
    select database() into sSelectedDatabase;   
   
    /* Adding new column  parent_topic_row_id to notification_alert_api */
    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_alert_api' and lower(column_name) = 'parent_topic_row_id') then   
      alter table notification_alert_api add column parent_topic_row_id int(11) not null;
    end if;
   
    start transaction;
	
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_SUCCESS');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_FAILED');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_SUCCESS');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_FAILED');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'VALIDATION_RUN_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_SUCCESS');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'VALIDATION_RUN_STATUS') where topic_row_id =(select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_FAILED');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_SCHEMA_COMPLETE') where topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_SCHEMA_COMPLETE');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_SCHEMA_FAILURE') where topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_SCHEMA_FAILURE');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_APPGROUP_COMPLETE') where topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_APPGROUP_COMPLETE');
	update notification_alert_api set parent_topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_APPGROUP_FAILURE') where topic_row_id =(select row_id from notification_topics where topic_title = 'RUN_APPGROUP_FAILURE');
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

