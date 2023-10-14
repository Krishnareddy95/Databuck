/* Add field triggeredByHost for load balancing  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	/* Add column triggeredByHost to table runScheduledTasks (Validation queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'runscheduledtasks' and lower(column_name) = 'triggeredbyhost') then	
		alter table runScheduledTasks
			add column triggeredByHost varchar(2500) default null;
	end if;
	
	/* Add column triggeredByHost to table runTemplateTasks (Template queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'runtemplatetasks' and lower(column_name) = 'triggeredbyhost') then	
		alter table runTemplateTasks
			add column triggeredByHost varchar(2500) default null;
	end if;
	
	/* Add column triggeredByHost to table schema_jobs_queue (Connection Job queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'schema_jobs_queue' and lower(column_name) = 'triggeredbyhost') then	
		alter table schema_jobs_queue
			add column triggeredByHost varchar(2500) default null;
	end if;
	
	/* Add column triggeredByHost to table appgroup_jobs_queue (AppGroup queue) */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'appgroup_jobs_queue' and lower(column_name) = 'triggeredbyhost') then	
		alter table appgroup_jobs_queue
			add column triggeredByHost varchar(2500) default null;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;