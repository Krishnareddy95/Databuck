/*
 * Adding new events inside alert_event_master
 */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if exists (select 1 from  alert_event_master where event_name='DQ_Validation_Success') then
            update alert_event_master set event_message_subject='Validation -[{FocusObjectId}] execution status',event_message_body='Validation with Id - [{FocusObjectId}] and name -[{Name}] ran successfully.\nData Quality Score:{dqi}.' where event_name='DQ_Validation_Success';
            end if;
    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;