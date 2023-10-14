/* --------------- Adding columns for locationMapping - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'locationMapping' and lower(COLUMN_NAME) = 'sourceType') then	
		alter table locationMapping
			add column sourceType varchar(1000);
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'locationMapping' and lower(COLUMN_NAME) = 'source') then	
		alter table locationMapping
			add column source varchar(1000);
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'locationMapping' and lower(COLUMN_NAME) = 'fileName') then	
		alter table locationMapping
			add column fileName varchar(1000);
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;