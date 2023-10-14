
/* Add master tags */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use(
	sTagId varchar(255)
)
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

	start transaction;

   if not exists (select 1 from notification_tags_master where upper(tag_id) = upper(sTagId)) then
   	insert into notification_tags_master (tag_id) values (sTagId);
	end if;

	commit; 

end $$
delimiter ;

call dummy_do_not_use('ValidationCreateStatus');
call dummy_do_not_use('MicrosegValidationCreateStatus');

drop procedure if exists dummy_do_not_use;


/* Create relations */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use(
		sTopicTitle varchar(255),
		nFocusType tinyint,
		sTagId varchar(100)
	)
begin
   declare sSelectedDatabase varchar(100) default '';
   declare nTopicRowId int default 0;
   declare nTagRowId int default 0;
   
   select database() into sSelectedDatabase;   
	
	/* Get row ids pairs to be inserted in relations table */
	select case 
		when not exists (select 1 from notification_topics where topic_title = sTopicTitle and focus_type = nFocusType) then -1
		else (select row_id from notification_topics where topic_title = sTopicTitle and focus_type = nFocusType)
   end into nTopicRowId; 	

	select case 
		when not exists (select 1 from notification_tags_master where tag_id = sTagId) then -1
		else (select row_id from notification_tags_master where tag_id = sTagId)
   end into nTagRowId; 	

	if ( (nTopicRowId > 0) && (nTagRowId > 0) ) then
		
		start transaction;
		
		if not exists (select 1 from notification_applicable_tags where topic_row_id = nTopicRowId and tag_row_id = nTagRowId) then
			insert into notification_applicable_tags (topic_row_id, tag_row_id) values (nTopicRowId, nTagRowId);
		end if;
		
		commit;
	end if;
	
end $$
delimiter ;

/* Applicable tags to 'CREATE_TEMPLATE_STATUS' */
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'ValidationCreateStatus');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'MicrosegValidationCreateStatus');

drop procedure if exists dummy_do_not_use;


/* Change Manged by for CREATE_TEMPLATE_STATUS and update the subject and body */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare nTopicRowId int default 0;
	
	start transaction;
	select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_STATUS' and focus_type = '1' into nTopicRowId;
		set nTopicRowId = ifnull(nTopicRowId,-1);
		
		if (nTopicRowId > -1) then
			update notification_topic_versions set message_subject="DataTemplate - [{FocusObjectId}] creation status.", message_body="Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{Status}] for DataTemplate with id - [ {FocusObjectId} ].\nValidation (Non Microsegment) creation is {ValidationCreateStatus}.\nValidation (Microsegment) creation is {MicrosegValidationCreateStatus} "  where topic_row_id = nTopicRowId;  
		end if;		
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
