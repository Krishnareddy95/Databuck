/* Add fields deployMode,processId,sparkAppId,startTime,endTime for load balancing  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
		
	/* Add column deployMode to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'deploymode') then	
		alter table schema_jobs_queue
			add column deployMode varchar(250) default null;
	end if;
	
	/* Add column deployMode to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'deploymode') then	
		alter table appgroup_jobs_queue
			add column deployMode varchar(250) default null;
	end if;
	
	/* Add column deployMode to table appgroup_jobs_queue (Project queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project_jobs_queue' and lower(column_name) = 'deploymode') then	
		alter table project_jobs_queue
			add column deployMode varchar(250) default null;
	end if;
	
	/* Add column processId to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'processid') then	
		alter table schema_jobs_queue
			add column processId bigint(20) default null;
	end if;
	
	/* Add column processId to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'processid') then	
		alter table appgroup_jobs_queue
			add column processId bigint(20) default null;
	end if;
	
	/* Add column processId to table appgroup_jobs_queue (Project queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project_jobs_queue' and lower(column_name) = 'processid') then	
		alter table project_jobs_queue
			add column processId bigint(20) default null;
	end if;
	
	/* Add column sparkAppId to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'sparkappid') then	
		alter table schema_jobs_queue
			add column sparkAppId varchar(1000) default null;
	end if;
	
	/* Add column sparkAppId to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'sparkappid') then	
		alter table appgroup_jobs_queue
			add column sparkAppId varchar(1000) default null;
	end if;
	
	/* Add column sparkAppId to table appgroup_jobs_queue (Project queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project_jobs_queue' and lower(column_name) = 'sparkappid') then	
		alter table project_jobs_queue
			add column sparkAppId varchar(1000) default null;
	end if;
	
	/* Add column startTime to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'starttime') then	
		alter table schema_jobs_queue
			add column startTime datetime default null;
	end if;
	
	/* Add column startTime to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'starttime') then	
		alter table appgroup_jobs_queue
			add column startTime datetime default null;
	end if;
	
	/* Add column startTime to table appgroup_jobs_queue (Project queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project_jobs_queue' and lower(column_name) = 'starttime') then	
		alter table project_jobs_queue
			add column startTime datetime default null;
	end if;
	
	/* Add column endTime to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'endtime') then	
		alter table schema_jobs_queue
			add column endTime datetime default null;
	end if;
	
	/* Add column endTime to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'endtime') then	
		alter table appgroup_jobs_queue
			add column endTime datetime default null;
	end if;
	
	/* Add column endTime to table appgroup_jobs_queue (Project queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'project_jobs_queue' and lower(column_name) = 'endtime') then	
		alter table project_jobs_queue
			add column endTime datetime default null;
	end if;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;