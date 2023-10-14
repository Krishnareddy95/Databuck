drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${appdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'appconfig' and lower(column_name) = 'numberapp') then
		/* Add column */
		ALTER table appconfig add column numberapp int;
	end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
