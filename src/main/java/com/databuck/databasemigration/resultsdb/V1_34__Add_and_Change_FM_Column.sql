drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    /* Add column */

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_load_history_table' and lower(column_name) = 'file_name') then

        ALTER table dbk_fm_load_history_table add column file_name varchar(10);
	end if;
    

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_filearrival_details' and lower(column_name) = 'file_name') then

		ALTER table dbk_fm_filearrival_details add column file_name varchar(10);
	end if;


    /* change column */

    if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_load_history_table' and lower(column_name) = 'table_name') then
		
        ALTER TABLE dbk_fm_load_history_table CHANGE COLUMN table_name  table_or_subfolder_name varchar(1000);
	end if;


	if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_filearrival_details' and lower(column_name) = 'table_or_file_name') then

		ALTER TABLE dbk_fm_filearrival_details CHANGE COLUMN table_or_file_name  table_or_subfolder_name varchar(1000);
	end if;
	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

