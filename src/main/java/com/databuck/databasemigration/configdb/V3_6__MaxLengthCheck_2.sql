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
        ALTER TABLE listDataDefinition ALTER COLUMN maxLengthCheck SET DEFAULT 'N';
        update listDataDefinition set maxLengthCheck='N' where maxLengthCheck IS NULL;
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listdatadefinition' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE staging_listDataDefinition ALTER COLUMN maxLengthCheck SET DEFAULT 'N';
        update staging_listDataDefinition set maxLengthCheck='N' where maxLengthCheck IS NULL;
    end if;

    if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE listApplications ALTER COLUMN maxLengthCheck SET DEFAULT 'N';
        update listApplications set maxLengthCheck='N' where maxLengthCheck IS NULL;
    end if;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'staging_listapplications' and lower(column_name) = 'maxlengthcheck') then
        ALTER TABLE staging_listApplications
            ADD COLUMN maxLengthCheck varchar(1) DEFAULT 'N';
    end if;


	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

