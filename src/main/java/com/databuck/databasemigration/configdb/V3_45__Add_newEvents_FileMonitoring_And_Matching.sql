/*
 * Adding new events inside alert_event_master
 */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if not exists (select 1 from alert_event_master where event_name='FileMonitoring_File_Missing') then
	    insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
              values("FileMonitoring_File_Missing","FileMonitoring","Technical",113,"FileMonitoring_Failure","Run_Failure","Hi {{User}},\nFile monitoring failed for Validation with Id - [{FocusObjectId}] and name - [{Name}] due to missing file [{fileName}] with missing count [{missingCount}].For more details click on this http://{databuckBaseUrl}/databuck/fileMonitorResults?idApp={FocusObjectId}&appName={Name}","FileMonitoring","FileMonitoring - [{FocusObjectId}] execution status");
	end if;
	
    if not exists (select 1 from alert_event_master where event_name='FileMonitoring_Schema_Failure') then
        insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
              values("FileMonitoring_Schema_Failure","FileMonitoring","Technical",114,"FileMonitoring_Failure","Run_Failure","Hi {{User}},\nFile monitoring failed for Validation with Id - [{FocusObjectId}] and name - [{Name}] due to schema mismatch for file [{fileName}].For more details click on this http://{databuckBaseUrl}/databuck/fileMonitorResults?idApp={FocusObjectId}&appName={Name}","FileMonitoring","FileMonitoring - [{FocusObjectId}] execution status");
    end if;

    if not exists (select 1 from alert_event_master where event_name='FileMonitoring_New_File') then
            insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
                  values("FileMonitoring_New_File","FileMonitoring","Technical",114,"FileMonitoring_Failure","Run_Failure","Hi {{User}},\nFile monitoring failed for Validation with Id - [{FocusObjectId}] and name - [{Name}] due to New file [{fileName}] detection.For more details click on this http://{databuckBaseUrl}/databuck/fileMonitorResults?idApp={FocusObjectId}&appName={Name}","FileMonitoring","FileMonitoring - [{FocusObjectId}] execution status");
        end if;

    if not exists (select 1 from alert_event_master where event_name='Data_Matching_Success') then
        insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
              values("Data_Matching_Success","DataMatching","Business",115,"DataMatching_Success","Run_Success","Hi {{User}},\nDataMatching successful for Validation with Id - [{FocusObjectId}] and name - [{Name}] with Left Only Records-[{LeftOnlyRecords}],Right Only Records-[{RightOnlyRecords}] And Unmatched Records-[{UnmatchedRecords}].","DataMatching","DataMatching - [{FocusObjectId}] execution status");
    end if;

    if not exists (select 1 from alert_event_master where event_name='Data_Matching_Failure') then
        insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
              values("Data_Matching_Failure","DataMatching","Technical",116,"DataMatching_Failure","Run_Failure","Hi {{User}},\nDataMatching failed for Validation with Id - [{FocusObjectId}] and name - [{Name}] with Left Only Records-[{LeftOnlyRecords}],Right Only Records-[{RightOnlyRecords}] And Unmatched Records-[{UnmatchedRecords}].","DataMatching","DataMatching - [{FocusObjectId}] execution status");
    end if;
	
	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;