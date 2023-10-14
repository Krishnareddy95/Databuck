drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	/* Correct the 'Successful' spelling in the alert message */
   	 update notification_alert_api set alert_msg='Connection successful' where topic_row_id=(select row_id from notification_topics where topic_title='CREATE_CONNECTION_SUCCESS');
   	 update notification_alert_api set alert_msg='Template successful' where topic_row_id=(select row_id from notification_topics where topic_title='CREATE_TEMPLATE_SUCCESS');
   	 update notification_alert_api set alert_msg='Validation successful' where topic_row_id=(select row_id from notification_topics where topic_title='DQ_VALIDATION_RUN_SUCCESS');
   	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
 