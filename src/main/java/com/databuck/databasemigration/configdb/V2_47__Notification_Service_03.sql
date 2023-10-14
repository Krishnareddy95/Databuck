/*
   2-Feb-2021 Due to SQL script error in V2_45__Notification_Service_03.sql - this version is added to correct problem
*/

/* Add additional tables for stage 2 development */
create table if not exists notification_tags_master (
   row_id               int(11) not null auto_increment primary key,
   tag_id               varchar(100) not null,
   tag_description      varchar(255) default '',
   active               bit not null default 1,
   unique key unique_notification_tag (tag_id)
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists notification_applicable_tags (
   row_id               int(11) not null auto_increment primary key,
   topic_row_id         int(11) not null,
   tag_row_id           int(11) not null,
   active               bit not null default 1,
   unique key unique_topic_tag_mapping (topic_row_id, tag_row_id)
) engine=innodb auto_increment=1 default charset=latin1;

/* delete all previous data so all environment get all data properly via this correcive script */
truncate table notification_applicable_tags;
truncate table notification_tags_master;

/* Add additional fields for stage 2 development */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'active') then   
      alter table notification_topics
         add column active bit default 1;
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'managed_by') then   
      alter table notification_topics
         add column managed_by tinyint default 0;
   end if;   

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

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

call dummy_do_not_use('FocusObjectId');
call dummy_do_not_use('ConnectionName');
call dummy_do_not_use('ConnectionType');

call dummy_do_not_use('DataTemplateName');
call dummy_do_not_use('DataTemplateDescription');
call dummy_do_not_use('DataLocation');
call dummy_do_not_use('DataSource');
call dummy_do_not_use('DataTableName');

call dummy_do_not_use('ValidationName');
call dummy_do_not_use('ValidationDescription');
call dummy_do_not_use('ValidationType');
call dummy_do_not_use('ValidationApplyRules');
call dummy_do_not_use('ValidationTemplateName');

call dummy_do_not_use('FileName');
call dummy_do_not_use('Status');
call dummy_do_not_use('User');

drop procedure if exists dummy_do_not_use;


/* Add umbrella notification topics */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use(
		sTopicToCreate varchar(255), 
		nFocusType int,
		sSubject varchar(255),
		sMessageBody text,
		lDeleteExistingData bit
	)
begin
   declare sSelectedDatabase varchar(100) default '';
   declare nTopicRowId int default 0;
   
   select database() into sSelectedDatabase;
   
   /* Delete existing data as per parameter */
   if (lDeleteExistingData > 0) then
   	start transaction;
		
		delete from notification_topic_versions where topic_row_id = (select row_id from notification_topics where topic_title = sTopicToCreate and focus_type = nFocusType);
		delete from notification_topics where topic_title = sTopicToCreate and focus_type = nFocusType;   
		
		commit;
   end if;
	
	/* Insert tags data into new tables and create relations which tags applicable to which topics */
	select case 
		when not exists (select 1 from notification_topics where topic_title = sTopicToCreate and focus_type = nFocusType) then -1
		else (select row_id from notification_topics where topic_title = sTopicToCreate and focus_type = nFocusType)
   end into nTopicRowId; 	

	if (nTopicRowId < 0) then
		start transaction;
		
		insert into notification_topics (topic_title, focus_type) values (upper(sTopicToCreate), nFocusType);
		
		select row_id from notification_topics where topic_title = sTopicToCreate and focus_type = nFocusType into nTopicRowId;
		set nTopicRowId = ifnull(nTopicRowId,-1);
	
		if (nTopicRowId > -1) then
			insert into notification_topic_versions
				(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
			values
				(
					nTopicRowId, 0, 1, 1, 0, '', sSubject, sMessageBody
				);
		end if;		
		
		commit;
	end if;
end $$
delimiter ;

call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'Default Subject', 'Default Body',1);
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'Default Subject', 'Default Body',1);
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'Default Subject', 'Default Body',1);
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
		
		insert into notification_applicable_tags (topic_row_id, tag_row_id) values (nTopicRowId, nTagRowId);
		
		commit;
	end if;
	
	start transaction;
	update notification_topics set managed_by = 1;
	update notification_topics set managed_by = 0 where topic_title in ('CREATE_CONNECTION_STATUS','CREATE_TEMPLATE_STATUS','VALIDATION_RUN_STATUS');  
	commit;
	
end $$
delimiter ;


/* Applicable tags to 'CREATE_CONNECTION_STATUS' */
call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'FocusObjectId');
call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'ConnectionName');
call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'ConnectionType');

call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'Status');
call dummy_do_not_use('CREATE_CONNECTION_STATUS',0,'User');

/* Applicable tags to 'CREATE_TEMPLATE_STATUS' */
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'FocusObjectId');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'DataTemplateName');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'DataTemplateDescription');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'DataSource');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'DataTableName');

call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'Status');
call dummy_do_not_use('CREATE_TEMPLATE_STATUS',1,'User');

/* Applicable tags to 'VALIDATION_RUN_STATUS' */
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'FocusObjectId');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'ValidationName');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'ValidationDescription');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'ValidationType');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'ValidationApplyRules');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'ValidationTemplateName');

call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'Status');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'User');
call dummy_do_not_use('VALIDATION_RUN_STATUS',2,'FileName');

drop procedure if exists dummy_do_not_use;

