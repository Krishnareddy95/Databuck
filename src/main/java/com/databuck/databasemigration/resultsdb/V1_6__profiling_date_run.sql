/* --------------- Adding columns for profiling tables - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_combination_profile_master_table' and lower(COLUMN_NAME) = 'Run') then	
		alter table column_combination_profile_master_table
			add column Run int(11) after id;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_combination_profile_master_table' and lower(COLUMN_NAME) = 'Date') then	
		alter table column_combination_profile_master_table
			add column Date date after id;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_detail_master_table' and lower(COLUMN_NAME) = 'Run') then	
		alter table column_profile_detail_master_table
			add column Run int(11) after id;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_detail_master_table' and lower(COLUMN_NAME) = 'Date') then	
		alter table column_profile_detail_master_table
			add column Date date after id;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_master_table' and lower(COLUMN_NAME) = 'Run') then	
		alter table column_profile_master_table
			add column Run int(11) after id;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_master_table' and lower(COLUMN_NAME) = 'Date') then	
		alter table column_profile_master_table
			add column Date date after id;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'numerical_profile_master_table' and lower(COLUMN_NAME) = 'Run') then	
		alter table numerical_profile_master_table
			add column Run int(11) after id;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'numerical_profile_master_table' and lower(COLUMN_NAME) = 'Date') then	
		alter table numerical_profile_master_table
			add column Date date after id;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'row_profile_master_table' and lower(COLUMN_NAME) = 'Run') then	
		alter table row_profile_master_table
			add column Run int(11) after id;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'row_profile_master_table' and lower(COLUMN_NAME) = 'Date') then	
		alter table row_profile_master_table
			add column Date date after id;
	end if;
	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;