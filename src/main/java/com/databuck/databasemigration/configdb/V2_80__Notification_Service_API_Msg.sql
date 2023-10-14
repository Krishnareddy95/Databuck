/* --------------- Updating focus type values for topics 'RUN_APPGROUP_COMPLETE' & 'RUN_APPGROUP_FAILURE' -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
    declare sSelectedDatabase varchar(100) default '';
    select database() into sSelectedDatabase;   
    
    start transaction;
	
	update notification_topics set focus_type = 3 where topic_title in ( 'RUN_APPGROUP_COMPLETE', 'RUN_APPGROUP_FAILURE' );
	update notification_topics set focus_type = 4 where topic_title in ( 'RUN_SCHEMA_COMPLETE', 'RUN_SCHEMA_FAILURE' ) ;
	update notification_project_subscriptions set focus_type = 3 where topic_row_id in (select row_id from notification_topics where topic_title in ( 'RUN_APPGROUP_COMPLETE', 'RUN_APPGROUP_FAILURE' ) );
	update notification_project_subscriptions set focus_type = 4 where topic_row_id in (select row_id from notification_topics where topic_title in ( 'RUN_SCHEMA_COMPLETE', 'RUN_SCHEMA_FAILURE' ) );

	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

