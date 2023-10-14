create procedure dummy_do_not_use()
language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin
            if  exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listdatadefinition' and lower(column_name) = 'defaultvalues') then
                 alter table listDataDefinition alter column defaultValues type varchar(100);
            end if;

            if  exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'staging_listdatadefinition' and lower(column_name) = 'defaultvalues') then
                 alter table staging_listdatadefinition alter column defaultValues type varchar(100);
            end if;


        	end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;


