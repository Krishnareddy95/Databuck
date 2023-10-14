/*
	08-Mar-2021 New Notification Topic Added for FILE MONITORING PROCESS FAILED	
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   declare nTopicRowId int default -1;
   declare nTagRowId int default -1;
   declare nExistingCount int default 0;
   declare sTopicTitle varchar(255) default 'FILE_MONITORING_PROCESS_FAILED';
   declare sTag varchar(25) default 'msg';
   
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
				"Failed file alert: {FileName}.", "Hi {{User}},\n{msg}."
			);
		commit;			
	end if;
	
	set nTagRowId = (select row_id from notification_tags_master where tag_id = sTag);
	set nTagRowId = ifnull(nTagRowId,-1);
	
	if (nTagRowId < 0) then
		start transaction;
		insert into notification_tags_master (tag_id, active) values (sTag, 1);	
		commit;			
	end if;
	
	set nTagRowId = (select row_id from notification_tags_master where tag_id = sTag);
	set nExistingCount = (select count(*) from notification_applicable_tags where topic_row_id = nTagRowId);
	
	if (nExistingCount < 1) then
		start transaction;
		insert into notification_applicable_tags (topic_row_id, tag_row_id, active) values (nTopicRowId, 14, 1);	
		insert into notification_applicable_tags (topic_row_id, tag_row_id, active) values (nTopicRowId, 16, 1);
		insert into notification_applicable_tags (topic_row_id, tag_row_id, active) values (nTopicRowId, nTagRowId, 1);
		commit;			
	end if;
	
	

	

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;