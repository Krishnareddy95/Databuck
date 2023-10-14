/* --------------- Creating tables for Alert Notifications -------------------------------- */

CREATE TABLE IF NOT EXISTS alert_event_master (
  event_id INT(11) NOT NULL AUTO_INCREMENT,
  event_name VARCHAR(1000) ,
  event_module_name VARCHAR(1000) ,
  event_communication_type VARCHAR(255) ,
  event_message_code INT ,
  event_completion_message VARCHAR(1000) ,
  event_completion_status VARCHAR(100) ,
  event_message_body text ,
  event_focus_object VARCHAR(100) ,
   event_message_subject VARCHAR(100) ,
  PRIMARY KEY (event_id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS alert_communication_modes (
  comm_mode_id INT NOT NULL AUTO_INCREMENT,
  comm_mode_name VARCHAR(255) ,
  comm_mode_description VARCHAR(1000) ,
  PRIMARY KEY (comm_mode_id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS alert_event_subscriptions (
  alert_sub_id INT(11) NOT NULL AUTO_INCREMENT,
  project_id INT(11) ,
  event_id INT ,
  comm_mode_id INT ,
  is_global_subscription VARCHAR(10) ,
  communication_values VARCHAR(255) ,
  PRIMARY KEY (alert_sub_id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS databuck_alert_log (
  alert_log_id INT(11) NOT NULL AUTO_INCREMENT,
  alert_publish_date date ,
  job_execution_date date ,
  task_unique_id VARCHAR(1000) ,
  job_run_number INT  ,
  project_id INT(11) ,
  event_id INT ,
  task_id INT(11) ,
  task_name VARCHAR(1000),
  alert_message text,
  is_event_published VARCHAR(10),
  is_event_subscribed VARCHAR(10),
  PRIMARY KEY (alert_log_id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


/*
	01-Nov-2022 Adding Alert Events and Communication modes
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';
   declare nExistingCount int default 0;

   select database() into sSelectedDatabase;

	set nExistingCount = (select count(*) from alert_event_master);

	if (nExistingCount <= 0) then
		start transaction;

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("Connection_Creation_Success","Connection","Business",101,"Connection_Success","Creation_Success","Hi {{User}},\nConnection '{Name}' is successfully created","Connection","Connection - [{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		values("Connection_Creation_Failure","Connection","Technical",102,"Connection_Failure","Creation_Failure","Hi {{User}},\nFailed to create Connection '{Name}'","Connection","Connection - [{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("Template_Creation_Success","Template","Business",103,"Template_Success","Creation_Success","Hi {{User}},\nData Analysis and Profiling Status - for DataTemplate with id - [{FocusObjectId}] and name - [{Name}] is [{status}].","Template","DataTemplate - [{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("Template_Creation_Failure","Template","Technical",104,"Template_Failure","Creation_Failure","Hi {{User}},\nData Analysis and Profiling Status - for DataTemplate with id - [{FocusObjectId}] and name - [{Name}] is [{status}].","Template","DataTemplate - [{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("Validation_Creation_Success","Validation","Business",105,"Validation_Success","Creation_Success","Hi {{User}},\nValidation with Id - [{FocusObjectId}] and name -[{Name}] is successfully created.","Validation","Validation -[{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("Validation_Creation_Failure","Validation","Technical",106,"Validation_Failure","Creation_Failure","Hi {{User}},\nFailed to create Validation with Id - [{FocusObjectId}] and name - [{Name}].","Validation","Validation -[{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("DQ_Validation_Success","Validation","Business",107,"DQ_Validation_Success","Run_Success","Validation with Id - [{FocusObjectId}] and name -[{Name}] ran successfully.\nData Quality Score:{dqi}.For more details click on this http://{databuckBaseUrl}/databuck/dashboard_table?idApp={FocusObjectId} .","Validation","Validation -[{FocusObjectId}] creation status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("DQ_Validation_Failure","Validation","Technical",108,"DQ_Validation_Failure","Run_Failure","Hi {{User}},\nFailed to run the Validation with Id - [{FocusObjectId}] and name - [{Name}].","Validation","Validation - [{FocusObjectId}] execution status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("RunSchema_Success","Schema","Business",109,"RunSchema_Success","Run_Success","Hi {{User}},\nSchema job ran successfully with id - [{FocusObjectId}] and name - [{Name}].","Schema","Schema - [{FocusObjectId}] execution status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("RunSchema_Failure","Schema","Technical",110,"RunSchema_Failure","Run_Failure","Hi {{User}},\nFailed to run Schema job with id - [{FocusObjectId}] and name - [{Name}].","Schema","Schema - [{FocusObjectId}] execution status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("RunAppGroup_Success","AppGroup","Business",111,"RunAppGroup_Success","Run_Success","Hi {{User}},\nAppGroup job ran successfully with id - [{FocusObjectId}] and name - [{Name}].","AppGroup",".AppGroup - [{FocusObjectId}] execution status");

		insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
		  values("RunAppGroup_Failure","AppGroup","Technical",112,"RunAppGroup_Failure","Run_Failure","Hi {{User}},\nFailed to run AppGroup job with id - [{FocusObjectId}] and name - [{Name}].","AppGroup",".AppGroup - [{FocusObjectId}] execution status");

	    commit;
	end if;

	set nExistingCount = (select count(*) from alert_communication_modes);

	if (nExistingCount <= 0) then
		start transaction;

		insert into alert_communication_modes(comm_mode_name,comm_mode_description) values ("EMAIL","an alert will be sent to subscribed email");
		insert into alert_communication_modes(comm_mode_name,comm_mode_description) values ("JIRA","Jira Ticket will be created in specified Project in Jira account");
		insert into alert_communication_modes(comm_mode_name,comm_mode_description) values ("SLACK","An alert Event Message will be publsihed to given slack channel");
		insert into alert_communication_modes(comm_mode_name,comm_mode_description) values ("SNS","An alert notification will be sent to AWS SNS topic");
		insert into alert_communication_modes(comm_mode_name,comm_mode_description) values ("SQS","An alert notification will be sent to AWS SQS Queue");

	    commit;
	end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;