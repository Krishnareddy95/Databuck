/* 
	01-Dec-2021 - Max Length check feature changes updates.
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdatadefinition' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE listDataDefinition MODIFY maxLengthCheck varchar(10) NOT NULL DEFAULT 'N';
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listdatadefinition' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE staging_listDataDefinition MODIFY maxLengthCheck varchar(10) NOT NULL DEFAULT 'N';
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE listApplications MODIFY maxLengthCheck varchar(10) NOT NULL DEFAULT 'N';
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplications' and lower(column_name) = 'maxlengthcheck') then
       ALTER TABLE staging_listApplications MODIFY maxLengthCheck varchar(10) NOT NULL DEFAULT 'N';
    end if;


	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

