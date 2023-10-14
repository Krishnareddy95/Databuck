/*
 * Update description of csvWriteLimit.
 */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
	    UPDATE alert_event_master SET event_message_body='Hi {{User}},\nTable {tableName}, with validation id {FocusObjectId} and name {Name}, executed on {executionDate} and run# {runNo} has a DTS of {dqi} and {failedChecks} checks failed.' WHERE event_name in ('DQ_Validation_Success', 'DQ_Validation_Failure') ;
    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;