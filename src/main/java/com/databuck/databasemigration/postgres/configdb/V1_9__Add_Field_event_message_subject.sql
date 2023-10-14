drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'databuck_alert_log' and lower(column_name) = 'event_message_subject') then
    	/* Add column */
    	alter table databuck_alert_log add column event_message_subject varchar(1000) default '';
    end if;

	update alert_event_master set event_message_subject='AppGroup - [{FocusObjectId}] execution status' where event_name in ('RunAppGroup_Success','RunAppGroup_Failure');
end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
