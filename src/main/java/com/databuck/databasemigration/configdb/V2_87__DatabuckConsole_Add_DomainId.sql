drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	/* Add column domainId to dashboard_project_conn_list table */
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dashboard_project_conn_list' and lower(column_name) = 'domainid') then   	
   		delete from dashboard_project_conn_list;
   		alter table dashboard_project_conn_list add column domainId int(11) not null AFTER id;
   	end if;
   	
   	/* Add column domainId to dashboard_conn_app_list table */
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dashboard_conn_app_list' and lower(column_name) = 'domainid') then   
		delete from dashboard_conn_app_list;
   		alter table dashboard_conn_app_list add column domainId int(11) not null AFTER conn_app_id;
   	end if;
   	
	/* Add column domainId to dashboard_project_color_grade table */
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dashboard_project_color_grade' and lower(column_name) = 'domainid') then   
		delete from dashboard_project_color_grade;
   		alter table dashboard_project_color_grade add column domainId int(11) not null AFTER gradeId;
   	end if;
   	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;	