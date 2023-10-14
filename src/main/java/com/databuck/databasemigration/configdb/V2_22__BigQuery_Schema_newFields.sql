/* --------------- Adding columns New Schema Type [BIGQUERY] - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'bigQueryProjectName') then	
		alter table listDataSchema
			add column bigQueryProjectName varchar(2000) DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'privateKeyId') then	
		alter table listDataSchema
			add column privateKeyId varchar(2000) DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'privateKey') then	
		alter table listDataSchema
			add column privateKey varchar(2500) CHARACTER SET ascii DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'clientId') then	
		alter table listDataSchema
			add column clientId varchar(2000) DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'clientEmail') then	
		alter table listDataSchema
			add column clientEmail varchar(2000) DEFAULT NULL;
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataSchema' and lower(COLUMN_NAME) = 'datasetName') then	
		alter table listDataSchema
			add column datasetName varchar(2000) DEFAULT NULL;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;