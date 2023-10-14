create procedure dummy_do_not_use()
language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin
            if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'listdataschema' and lower(column_name) = 'azure_authentication_type') then
                /* Add column */
                 alter table listDataSchema add column azure_authentication_type varchar(100);
            end if;

        	end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
