drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${resultsdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'data_quality_dashboard' and lower(column_name) = 'iddataschema') then
        /* Add column */
        ALTER table data_quality_dashboard add column idDataSchema BIGINT DEFAULT NULL;
    end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = '${resultsdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'data_quality_dashboard' and lower(column_name) = 'foldername') then
        /* Add column */
        ALTER table data_quality_dashboard add column folderName varchar(50) default NULL;
    end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;