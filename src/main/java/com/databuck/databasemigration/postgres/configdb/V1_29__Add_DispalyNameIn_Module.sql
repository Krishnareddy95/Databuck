create or replace procedure dummy_do_not_use()
language plpgsql
as $$
     declare sSelectedDatabase varchar(100) := (select current_database());
begin
         if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'module' and lower(column_name) = 'displayname') then
            /* Add column */
             alter table module add column displayname  varchar(500);
        end if;

        if exists (select 1 from module) then
            update module set displayname=taskname;
        end if;

        if exists (select 1 from module where taskname ='Data Template') then
            update module set displayname ='Validation Templates'where taskname ='Data Template';
        end if;

         if exists (select 1 from module where taskname ='Global Rule') then
            update module set displayname ='Custom Rules' where taskname ='Global Rule';
        end if;

         if exists (select 1 from module where taskname ='Validation Check') then
            update module set displayname ='Rule Catalog' where taskname ='Validation Check';
        end if;

        if exists (select 1 from module where taskname ='QuickStart') then
            update module set displayname ='Quick Start' where taskname ='QuickStart';
        end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;