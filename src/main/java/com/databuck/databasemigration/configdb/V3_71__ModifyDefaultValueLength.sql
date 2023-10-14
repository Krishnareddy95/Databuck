/* 
	16-March-2023 - Update length of defaultValues column
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase;
	
	start transaction;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdatadefinition' and lower(column_name) = 'defaultValues') then
        ALTER TABLE listDataDefinition MODIFY defaultValues varchar(100);
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listdatadefinition' and lower(column_name) = 'defaultValues') then
        ALTER TABLE staging_listDataDefinition MODIFY defaultValues varchar(100);
    end if;
    
 commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

