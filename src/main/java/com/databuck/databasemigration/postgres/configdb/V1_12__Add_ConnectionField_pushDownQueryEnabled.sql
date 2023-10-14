drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listDataSchema' and lower(column_name) = 'push_down_query_enabled') then
    	/* Add column */
    	alter table listDataSchema add column push_down_query_enabled varchar(10) default 'N';
    end if;

    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
