/*
	12-Dec-2020 New Notification Service 
	
	Meta data based design to allow easy configurations and customizations across multiple customer's 
	different different requirements without much code change.   	
	
	Data Model DDL and seed data (hard coded calls converted into meta data) as of 12-Dec-2020 
*/	
create table if not exists notification_topics (
   row_id               int(11) not null auto_increment primary key,
   topic_title          varchar(255),
   focus_type           tinyint,
   unique key unique_topic_title (topic_title, focus_type)
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists notification_topic_versions (
   row_id               int(11) not null auto_increment primary key,
   topic_row_id         int(11) not null,
   topic_version        tinyint,
   is_selected          bit default 0,                                            
   is_email             bit default 0,
   is_sms               bit default 0,
   base_media_ids       varchar(1000),
   message_subject      varchar(1000),
   message_body         text,
   unique key unique_topic_version (topic_row_id, topic_version)
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists notification_project_subscriptions (
   row_id               int(11) not null auto_increment primary key,
   topic_row_id         int(11) not null,
   project_row_id       int(11) not null,   
   unique key unique_subcription (topic_row_id, project_row_id)
) engine=innodb auto_increment=1 default charset=latin1;

/* Table project => adding project wise new column email id for sending notification */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project' and lower(column_name) = 'notification_email') then   
      alter table project
         add column notification_email varchar(255) default null;
   end if;
   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* select module and role row ids into mysql session variables */
set @nDataConnRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Connection');
set @nDataTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Template');
set @nExtnTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Extend Template & Rule');
set @nValCheckRowId     = (select idTask from Module where upper(trim(taskName)) = 'Validation Check');
set @nTasksRowId        = (select idTask from Module where upper(trim(taskName)) = 'Tasks');
set @nResultsRowId      = (select idTask from Module where upper(trim(taskName)) = 'Results');
set @nUserSettingsRowId = (select idTask from Module where upper(trim(taskName)) = 'User Settings');
set @nGlobalRuleRowId   = (select idTask from Module where upper(trim(taskName)) = 'Global Rule');
set @nDashConfigRowId   = (select idTask from Module where upper(trim(taskName)) = 'Dash Configuration');
set @nDashboardRowId    = (select idTask from Module where upper(trim(taskName)) = 'Dashboard');
set @nAppSettingsRowId  = (select idTask from Module where upper(trim(taskName)) = 'Application Settings');

select 
	@nDataConnRowId, @nDataTmplRowId, @nExtnTmplRowId, @nValCheckRowId, @nTasksRowId, @nResultsRowId, 
	@nUserSettingsRowId, @nGlobalRuleRowId, @nDashConfigRowId, @nDashboardRowId, @nAppSettingsRowId;

set @nAdminRoleRowId = (select case when (select count(*) from Role where upper(trim(roleName)) = 'ADMIN') > 0 then (select idRole from Role where upper(trim(roleName)) = 'ADMIN') else -1 end as RoleRowId); 
set @sComponentName = 'Notification_Setup_Subcriptions';

/* insert component and access control for admin user to 'Notification_Setup_Subcriptions' */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 
   
	/* select module and role row ids into mysql session variables */
	set @nDataConnRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Connection');
	set @nDataTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Template');
	set @nExtnTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Extend Template & Rule');
	set @nValCheckRowId     = (select idTask from Module where upper(trim(taskName)) = 'Validation Check');
	set @nTasksRowId        = (select idTask from Module where upper(trim(taskName)) = 'Tasks');
	set @nResultsRowId      = (select idTask from Module where upper(trim(taskName)) = 'Results');
	set @nUserSettingsRowId = (select idTask from Module where upper(trim(taskName)) = 'User Settings');
	set @nGlobalRuleRowId   = (select idTask from Module where upper(trim(taskName)) = 'Global Rule');
	set @nDashConfigRowId   = (select idTask from Module where upper(trim(taskName)) = 'Dash Configuration');
	set @nDashboardRowId    = (select idTask from Module where upper(trim(taskName)) = 'Dashboard');
	set @nAppSettingsRowId  = (select idTask from Module where upper(trim(taskName)) = 'Application Settings');

	set @nAdminRoleRowId = (select case when (select count(*) from Role where upper(trim(roleName)) = 'ADMIN') > 0 then (select idRole from Role where upper(trim(roleName)) = 'ADMIN') else -1 end as RoleRowId); 
	set @sComponentName = 'Notification_Setup_Subcriptions'; 
	set @nComponentRowId = (select row_id from component where component_name = @sComponentName);

	start transaction;
	
   if (@nComponentRowId is null) then
		insert into component (component_name,component_title,component_type,module_row_id,http_url)	values (@sComponentName, 'Notification View', 0, @nUserSettingsRowId, '/notificationView');
		set @nComponentRowId = (select row_id from component where component_name = @sComponentName);
		
		/* insert all access rights for admin user */
		insert into component_access (role_row_id, component_row_id) values (@nAdminRoleRowId, @nComponentRowId); 		
   end if;
   
  	commit;   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* Insert data for notificaition topics in whole databuck code */
start transaction;

truncate table notification_topics;
truncate table notification_topic_versions;
truncate table notification_project_subscriptions;

commit;

/* Insert all seed data at time of new notification module goes live */
set @nTopicRowId = 0;

/* Connection */
insert into notification_topics (topic_title, focus_type) values ('CREATE_CONNECTION_SUCCESS', 0);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Connection - [{FocusObjectId}] creation status.", "Hi {{User}},\nConnection '{ConnName}' is successfully created."
	);

insert into notification_topics (topic_title, focus_type) values ('CREATE_CONNECTION_FAILED', 0);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_CONNECTION_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Connection - [{FocusObjectId}] creation status.", "Hi {{User}},\nFailed to create Connection '{ConnName}'."
	);

/* DataTemplate*/
insert into notification_topics (topic_title, focus_type) values ('CREATE_TEMPLATE_SUCCESS', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] creation status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ]."
	);

insert into notification_topics (topic_title, focus_type) values ('CREATE_TEMPLATE_FAILED', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_TEMPLATE_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] creation status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ]."
	);

insert into notification_topics (topic_title, focus_type) values ('PROFILING_ENABLED', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'PROFILING_ENABLED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] Profiling ReRun status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ]."
	);


insert into notification_topics (topic_title, focus_type) values ('AUTO_VALIDATON_CREATION_SUCCESS', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'AUTO_VALIDATON_CREATION_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] creation status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].
Validation Check with Id [{idApp}] is created Successfully for this template !!"
	);

insert into notification_topics (topic_title, focus_type) values ('AUTO_VALIDATON_CREATION_DISABLED', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'AUTO_VALIDATON_CREATION_DISABLED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] creation status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].
Automatic Validation Check creation is not enabled for this request !!"
	);

insert into notification_topics (topic_title, focus_type) values ('AUTO_VALIDATON_CREATION_FAILED', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'AUTO_VALIDATON_CREATION_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"DataTemplate - [{FocusObjectId}] creation status.", "Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].
Failed to create default Validation check for this template !!"
	);

/* Validation  */
insert into notification_topics (topic_title, focus_type) values ('CREATE_VALIDATION_SUCCESS', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_VALIDATION_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Validation -[{FocusObjectId}] creation status.", "Hi {{User}},\nValidation '{ValName}' is successfully created."
	);

insert into notification_topics (topic_title, focus_type) values ('CREATE_VALIDATION_FAILED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'CREATE_VALIDATION_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Validation -[{FocusObjectId}] creation status.", "Hi {{User}},\nFailed to create Validation '{ValName}'."
	);


insert into notification_topics (topic_title, focus_type) values ('VALIDATION_APPROVAL', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'VALIDATION_APPROVAL');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Validation Approval Process Notification.", "Hi {{User}},\nValidation Application ID:  {FocusObjectId} is ready for Test and Waiting for your Approval."
	);



/* File Monitring*/
insert into notification_topics (topic_title, focus_type) values ('FILE_MONITORING_FAILED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'FILE_MONITORING_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Failed file alert: {fileName}", "Hi {{User}},\nBelow are the status details of file checks for the file [  {folderPath} /  {fileName} ]\nZeroSizeFileCheck: {ZeroSizeFileCheck}\nRecordLengthCheck: {RecordLengthCheck}\nColumnCountCheck: {ColumnCountCheck}\nColumnSequenceCheck: {ColumnSequenceCheck}"
	);


insert into notification_topics (topic_title, focus_type) values ('FILE_MONITORING_SUCCESS', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'FILE_MONITORING_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"{MaxDate}: Rule Summary For Rule Id [{FocusObjectId}]", "Hi {{User}}, \nRule information of Rule Id [{FocusObjectId}] :\nFile Frequency: {FileFrequency}\nDay of check: {DayOfCheck}\nTime of check: {Time}\nFile Path: {FilePath}\nFile Pattern: {FilePattern}\n\nBelow are the Summary Details of Rule:\n\nExpected Files Count: {ExpectedCount}\nArrived Files Count: {ArrivedCount}\nMissing Files Count: {MissingCount}\nDuplicate Files Count: {DuplicateCount}"
	);


insert into notification_topics (topic_title, focus_type) values ('BATCH_VALIDATION_TEMPCREATION_FAILED', 1);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'BATCH_VALIDATION_TEMPCREATION_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Template not created for {ConnName}", "Hi {{User}},\nTemplate was not created because of duplicate column names in config file. Config file path: {ConfigFilePath} Table name: {TableName} Column name(s): {Columns}"
	);


insert into notification_topics (topic_title, focus_type) values ('DATA_MATCHING_EXE_FAILED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DATA_MATCHING_EXE_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Data Matching  Report for IdApp:[{FocusObjectId}]", "Hi {{User}},\nStatus: Data Matching validation ran for {FocusObjectId} failed due to exception. Check logs for details."
	);


insert into notification_topics (topic_title, focus_type) values ('ROLL_DATA_MATCHING_VALIDATION_EXE_FAILED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'ROLL_DATA_MATCHING_VALIDATION_EXE_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Data Matching  Report for IdApp:[{FocusObjectId}]", "Hi {{User}},\nStatus: Data Matching validation ran for {FocusObjectId} failed due to exception. Check logs for details."
	);

insert into notification_topics (topic_title, focus_type) values ('DQ_VALIDATION_RUN_SUCCESS', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_SUCCESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Data quality validation ran for {fileName} successfully.\nData Quality Score:\n For more details click on this link\n<host:port>/databuck/dashboard_table?idApp={FocusObjectId}"
	);
	
insert into notification_topics (topic_title, focus_type) values ('DQ_VALIDATION_RUN_FAILED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_FAILED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Data quality validation ran for {fileName} with idApp {FocusObjectId} failed."
	);

insert into notification_topics (topic_title, focus_type) values ('DQ_VALIDATION_RUN_INPROGRESS', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_INPROGRESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Data quality validation ran for idApp's {sb} is still in progress. Please check"
	);

insert into notification_topics (topic_title, focus_type) values ('DQ_VALIDATION_RUN_PROCESSING', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DQ_VALIDATION_RUN_PROCESSING');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Databuck Processing {status} for AppId {FocusObjectId}."
	);

insert into notification_topics (topic_title, focus_type) values ('DM_VALIDATION_RUN_UNMATCHED', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DM_VALIDATION_RUN_UNMATCHED');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Data Matching validation ran for {fileName} with idApp {FocusObjectId} and Application Name:: {appname} found unmatched."
	);

insert into notification_topics (topic_title, focus_type) values ('DM_VALIDATION_RUN_INPROGRESS', 2);
set @nTopicRowId = (select row_id from notification_topics where topic_title = 'DM_VALIDATION_RUN_INPROGRESS');

insert into notification_topic_versions
	(topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body)
values
	(
		@nTopicRowId, 0, 1, 1, 0, '', 
		"Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}", "Hi {{User}},\nStatus: Data Matching validation ran for idApp's {sb} and Application Name -{appname} is still in progress. Please check"
	);
