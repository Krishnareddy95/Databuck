drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

    if exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'synonymlibrary' and lower(column_name) = 'possiblenames') then
    	/* change datatype */
    	alter table SynonymLibrary alter column possiblenames type text;
    end if;

 end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;

