drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listschedule' and lower(column_name) = 'domain_id') then
    	/* Add column */
    	 alter table listSchedule add column domain_id int default 0;
    end if;

   end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;


