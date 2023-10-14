drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${resultsdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'app_option_list_elements' and lower(column_name) = 'elements2app_list') then
		/* Add column */
		ALTER table app_option_list_elements add column elements2app_list int;
	end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;
