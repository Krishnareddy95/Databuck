/* --------------- Creating table for schema_jobs_queue -------------------------------- */
DROP TABLE IF EXISTS `schema_jobs_queue`;

create table if not exists schema_jobs_queue (
   queueId int(11) not null primary key auto_increment,
   idDataSchema int(11) not null,   
   uniqueId bigint(20) not null, 
   status varchar(500),
   createdAt datetime
) engine=innodb auto_increment=1 default charset=latin1;


/* --------------- Creating table for schema_jobs_tracking -------------------------------- */
DROP TABLE IF EXISTS `schema_jobs_tracking`;

create table if not exists schema_jobs_tracking (
   id int(11) not null primary key auto_increment,
   idDataSchema int(11) not null,   
   uniqueId bigint(20) not null, 
   idData int(11) not null,
   template_uniqueId varchar(2000)
) engine=innodb auto_increment=1 default charset=latin1;


/* --------------- Adding and modifying columns in scheduledTasks -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'scheduledTasks' and lower(COLUMN_NAME) = 'idDataSchema') then	
		alter table scheduledTasks
			add column idDataSchema int(10);
	end if;

	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'scheduledTasks' and lower(COLUMN_NAME) = 'idApp') then	
		alter table scheduledTasks
			MODIFY idApp int(10) null;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

