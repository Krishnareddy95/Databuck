create procedure dummy_do_not_use()
language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin
            if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'logging_activity' and lower(column_name) = 'entity_id') then
                /* Add column */
                 alter table logging_activity add column entity_id  int default null;
            end if;

            if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'logging_activity' and lower(column_name) = 'entity_name') then
                /* Add column */
                 alter table logging_activity add column entity_name  varchar(255) default 'N';
            end if;

            if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'logging_activity' and lower(column_name) = 'activity_name') then
                /* Add column */
                 alter table logging_activity add column activity_name  varchar(255) default 'N';
            end if;

            if exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'logging_activity' and lower(column_name) = 'access_url') then
                	/* change datatype */
                alter table logging_activity alter column access_url drop not null;
            end if;

        	end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;

