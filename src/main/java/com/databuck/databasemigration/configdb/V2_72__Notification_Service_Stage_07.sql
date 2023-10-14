/*
	9-Apr-2021 Notification Service Enhancements (Generic for publishing DataBuck events to external application)	
	
	Each Customer implementation can write "centralized" event handler to recieve DataBuck publish events or as per their choice different events handlers. 	
	
	1) URL1 - Functional Databuck events (create connection, create template, validation completed etc)
	2) URL2 - Any other type say Netcool application availability events
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	declare nIndexExists int default 0;
	select database() into sSelectedDatabase; 	
	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'authorization') then	
		alter table notification_topics
			add column authorization varchar(100);
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'service_id') then	
		alter table notification_topics
			add column service_id varchar(100);
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'password') then	
		alter table notification_topics
			add column password varchar(100);
	end if;	

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_project_subscriptions' and lower(column_name) = 'focus_type') then	
		alter table notification_project_subscriptions
			add column focus_type tinyint(4) not null;	
	end if;	

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_project_subscriptions' and lower(column_name) = 'notification_email') then	
		alter table notification_project_subscriptions
			add column notification_email varchar(255) default null;
	end if;		
	
	set nIndexExists = (select count(*) from information_schema.statistics where index_schema = sSelectedDatabase and lower(table_name) = 'notification_project_subscriptions' and index_name = 'unique_subcription');
	if (nIndexExists > 0) then
		drop index unique_subcription on notification_project_subscriptions;
	end if;
	create unique index unique_subcription on notification_project_subscriptions (topic_row_id, focus_type, project_row_id);	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;







