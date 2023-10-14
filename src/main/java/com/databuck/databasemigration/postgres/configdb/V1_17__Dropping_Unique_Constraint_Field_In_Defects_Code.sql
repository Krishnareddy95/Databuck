/* Bug fix droping unique_constraint index */
drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin
    if  exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'defect_codes' and lower(column_name) = 'defect_code') then
    	/* DropCONSTRAINTS */
    	alter table defect_codes drop constraint if exists unique_defect_code;
    end if;

    if  exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'defect_codes' and lower(column_name) = 'defect_code') then
        	/* DropCONSTRAINTS */
         alter table defect_codes drop constraint if exists unique_defect_dimension;
    end if;
 end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;