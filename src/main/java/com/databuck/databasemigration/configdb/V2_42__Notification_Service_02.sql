/*
	18-Jan-2021 New Notification Topic Added for Exception Report CSV attachment sending	
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   declare nTopicRowId int default -1;   
   declare nExistingCount int default 0;
   declare sTopicTitle varchar(255) default 'EXCEPTION_REPORT';
   
   select database() into sSelectedDatabase;    
   
   set nTopicRowId = (select row_id from notification_topics where upper(topic_title) = sTopicTitle);
	set nTopicRowId = ifnull(nTopicRowId,-1);
	
	if (nTopicRowId < 0) then
		start transaction;
		insert into notification_topics (topic_title, focus_type) values (sTopicTitle, 2);	
		commit;			
	end if;

	set nTopicRowId = (select row_id from notification_topics where upper(topic_title) = sTopicTitle);
	set nExistingCount = (select count(*) from notification_topic_versions where topic_row_id = nTopicRowId and topic_version = 0); 
	
	if (nExistingCount < 1) then
		start transaction;
		insert into notification_topic_versions
			(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
		values
			(
				nTopicRowId, 0, 1, 1, 0, '', 
				"Row Summary for Validation APP ID : {FocusObjectId}.", "Hi {{User}},\nPlease find Attached Consolidated Row Summary for Validation APP ID : {FocusObjectId}."
			);
		commit;			
	end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


