/* --------------- Adding columns New Schema Type [AzureDataLake] - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataschema' and lower(COLUMN_NAME) = 'azureclientid') then	
		alter table listDataSchema
			add column azureClientId text DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataschema' and lower(COLUMN_NAME) = 'azureclientsecret') then	
		alter table listDataSchema
			add column azureClientSecret text DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataschema' and lower(COLUMN_NAME) = 'azuretenantid') then	
		alter table listDataSchema
			add column azureTenantId text DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataschema' and lower(COLUMN_NAME) = 'azureserviceuri') then	
		alter table listDataSchema
			add column azureServiceURI text DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataschema' and lower(COLUMN_NAME) = 'azurefilepath') then	
		alter table listDataSchema
			add column azureFilePath text DEFAULT NULL;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;