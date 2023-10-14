/*
 * Adding new events inside alert_event_master
 */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if not exists (select 1 from  alert_event_master where event_name='DQ_Validation_Reprofiling') then
            insert into alert_event_master (event_name,event_module_name,event_communication_type,event_message_code,event_completion_message,event_completion_status,event_message_body,event_focus_object,event_message_subject)
            values('DQ_Validation_Reprofiling','Validation','Technical',117,'DQ_Validation_Reprofiling','Run_Status','Hi {{User}},\nReprofiling for validation with id- [{FocusObjectId}] and name - [{Name}] is {status}','Validation','ValidationReprofiling - [{FocusObjectId}] execution status');
            end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;