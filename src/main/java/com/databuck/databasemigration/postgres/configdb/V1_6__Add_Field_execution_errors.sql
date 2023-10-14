drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

    if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'databuck_alert_log' and lower(column_name) = 'execution_errors') then
		/* Add column */
    	alter table databuck_alert_log add column execution_errors text default NULL;
    end if;
end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;

