/* --------------- Adding new column to listDataSources - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdatasources' and lower(COLUMN_NAME) = 'profilingrerunenabled') then	
		alter table listDataSources
			add column profilingReRunEnabled varchar(20) DEFAULT 'N';
	end if;
	
	
	start transaction;
	
	/* For all the existing active templates make profilingReRunEnabled='Y' */
	update listDataSources set profilingReRunEnabled='Y' where idData in (select idData from listDataAccess) and active='Yes' and dataLocation not in ('File Management','Kafka');
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;