/* --------------- Creating table for notification_alert_api -------------------------------- */
DROP TABLE IF EXISTS `notification_alert_api`;

create table if not exists `notification_alert_api` (
  row_id int(11) not null primary key AUTO_INCREMENT,
  topic_row_id int(11) not null unique,
  alert_msg text not null,
  alert_msg_code varchar(1000) not null,
  alert_label varchar(1000) not null
) engine=innodb default charset=latin1;


drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
   declare sSelectedDatabase varchar(100) default '';
   
   select database() into sSelectedDatabase;   
   
	start transaction;
	
	/* Add fields for Authorization type , username and password of second publish url2 */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'url2_authorization') then	
		alter table notification_topics
			add column url2_authorization varchar(100);
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'url2_service_id') then	
		alter table notification_topics
			add column url2_service_id varchar(100);
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'url2_password') then	
		alter table notification_topics
			add column url2_password varchar(100);
	end if;	
	
	/* create new topic for RunAppGroup Success and failure */
	set @topic_title = 'RUN_APPGROUP_FAILURE';
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
				"AppGroup - [{FocusObjectId}] execution status", "Hi {{User}},\nREPORT: AppGroup job with id - [{FocusObjectId}] execution Status is - [{Status}]."
			);
			
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'FocusObjectId'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'User'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'Status'));
			
		end if;		
		
	end if;
	
	set @topic_title = 'RUN_APPGROUP_COMPLETE';
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
				"AppGroup - [{FocusObjectId}] execution status", "Hi {{User}},\nREPORT: AppGroup job with id - [{FocusObjectId}] execution Status is - [{Status}]."
			);
			
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'FocusObjectId'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'User'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'Status'));
			
		end if;		
		
	end if;
	
	/* create new topic for RunSchema success and failure */
	set @topic_title = 'RUN_SCHEMA_FAILURE';
	if not exists (select 1 from notification_topics where topic_title= @topic_title) then
   		insert into notification_topics (topic_title, focus_type) values (@topic_title, 2);
   		
   		set @nTopicRowId = (select case when not exists (select 1 from notification_topics where topic_title = @topic_title) then -1
							else (select row_id from notification_topics where topic_title = @topic_title) end); 

		if (@nTopicRowId > 0) then
			insert into notification_topic_versions
			(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
			values
			(
				@nTopicRowId, 0, 1, 1, 0, '', 
				"Schema Job - [{FocusObjectId}] execution status", "Hi {{User}},\nREPORT: Schema job with id - [{FocusObjectId}] execution Status is - [{Status}]."
			);
			
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'FocusObjectId'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'User'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'Status'));
			
		end if;		
		
	end if;	

	set @topic_title = 'RUN_SCHEMA_COMPLETE';
	if not exists (select 1 from notification_topics where topic_title= @topic_title) then
   		insert into notification_topics (topic_title, focus_type) values (@topic_title, 2);
   		
   		set @nTopicRowId = (select case when not exists (select 1 from notification_topics where topic_title = @topic_title) then -1
							else (select row_id from notification_topics where topic_title = @topic_title) end); 

		if (@nTopicRowId > 0) then
			insert into notification_topic_versions
			(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
			values
			(
				@nTopicRowId, 0, 1, 1, 0, '', 
				"Schema Job - [{FocusObjectId}] execution status", "Hi {{User}},\nREPORT: Schema job with id - [{FocusObjectId}] execution Status is - [{Status}]."
			);
			
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'FocusObjectId'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'User'));
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (@nTopicRowId, (select row_id from notification_tags_master where tag_id= 'Status'));
			
		end if;		
		
	end if;	
	
	/* Insert API alert details for topic connection successful */
	set @topic_title = 'CREATE_CONNECTION_SUCCESS';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Connection sucessful','101','idDataSchema');
	end if;	
	
	/* Insert API alert details for topic connection failed */
	set @topic_title = 'CREATE_CONNECTION_FAILED';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Connection failed','102','idDataSchema');
	end if;	
	
	/* Insert API alert details for topic template successful */
	set @topic_title = 'CREATE_TEMPLATE_SUCCESS';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	

	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Template sucessful','103','idData');
	end if;	
	
	/* Insert API alert details for topic template failed */
	set @topic_title = 'CREATE_TEMPLATE_FAILED';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	

	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Template failed','104','idData');
	end if;	
	
	/* Insert API alert details for topic validation successful */
	set @topic_title = 'DQ_VALIDATION_RUN_SUCCESS';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	

	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Validation sucessful','105','idApp');
	end if;	
	
	/* Insert API alert details for topic validation failed */
	set @topic_title = 'DQ_VALIDATION_RUN_FAILED';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	

	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'Validation failed','4','idApp');
	end if;	
	
	/* Insert API alert details for topic runSchema success */
	set @topic_title = 'RUN_SCHEMA_COMPLETE';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'RunConnection completed ','106','idDataSchema');
	end if;	
	
	/* Insert API alert details for topic runSchema failed */
	set @topic_title = 'RUN_SCHEMA_FAILURE';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'RunConnection failed ','107','idDataSchema');
	end if;	
	
	/* Insert API alert details for topic runAppgroup success */
	set @topic_title = 'RUN_APPGROUP_COMPLETE';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'RunAppGroup completed','108','idAppGroup');
	end if;	
	
	/* Insert API alert details for topic runAppgroup failure */
	set @topic_title = 'RUN_APPGROUP_FAILURE';
	set @nListExists = (select count(*) from notification_alert_api where topic_row_id = (select row_id from notification_topics where topic_title = @topic_title));	
	
	if (@nListExists < 1) then	
		insert into notification_alert_api(topic_row_id,alert_msg,alert_msg_code,alert_label) values((select row_id from notification_topics where topic_title = @topic_title),'RunAppGroup failed','109','idAppGroup');
	end if;	
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
