drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    /* change column */

    if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_summary_details' and lower(column_name) = 'table_or_file_name') then
		
        ALTER TABLE dbk_fm_summary_details CHANGE COLUMN table_or_file_name  table_or_subfolder_name varchar(1000);
	end if;


	if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_filearrival_details' and lower(column_name) = 'file_name') then

		ALTER TABLE dbk_fm_filearrival_details MODIFY COLUMN file_name VARCHAR(1000);
	end if;


	if  exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dbk_fm_load_history_table' and lower(column_name) = 'file_name') then

        ALTER TABLE dbk_fm_load_history_table MODIFY COLUMN file_name VARCHAR(1000);
    end if;
	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

