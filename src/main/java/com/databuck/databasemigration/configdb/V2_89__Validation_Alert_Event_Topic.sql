
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare nTagRowId int default -1;
   declare sSelectedDatabase varchar(100) default '';
   
   select database() into sSelectedDatabase;   
   
	start transaction;
	
	/* create new topic for VALIDATION_OTHER_EVENTS */
	set @topic_title = 'VALIDATION_OTHER_EVENTS';
	if not exists (select 1 from notification_topics where topic_title = @topic_title) then
   		
		insert into notification_topics (topic_title, focus_type) values (@topic_title, 2);
   		
		set @nTopicRowId = (select case when not exists (select 1 from notification_topics where topic_title = @topic_title) then -1
							else (select row_id from notification_topics where topic_title = @topic_title) end); 

		if (@nTopicRowId > 0) then
			insert into notification_topic_versions
			(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
			values
			(
				@nTopicRowId, 0, 1, 1, 0, '', 
				"Validation - [{FocusObjectId}] Alert Event", "Hi {{User}},\nValidation job with id - [{FocusObjectId}] alert event details: \n{AlertEventMsg}."
			);
			
			/* Adding tag AlertEventMsg */
			set nTagRowId = (select row_id from notification_tags_master where tag_id = 'AlertEventMsg');
			set nTagRowId = ifnull(nTagRowId,-1);
			
			if (nTagRowId < 0) then
				insert into notification_tags_master (tag_id, active) values ('AlertEventMsg', 1);	
			end if;
	
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'FocusObjectId'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'User'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'AlertEventMsg'));
			
		end if;		
		
	end if;
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
