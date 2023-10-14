/* --------------- Adding columns for Roll Data Matching - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataAccess' and lower(COLUMN_NAME) = 'rollingHeader') then	
		alter table listDataAccess
			add column rollingHeader varchar(50) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listDataAccess' and lower(COLUMN_NAME) = 'rollingColumn') then	
		alter table listDataAccess
			add column rollingColumn varchar(50) default null;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listApplications' and lower(COLUMN_NAME) = 'rollTargetSchemaId') then	
		alter table listApplications
			add column rollTargetSchemaId int(11) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;