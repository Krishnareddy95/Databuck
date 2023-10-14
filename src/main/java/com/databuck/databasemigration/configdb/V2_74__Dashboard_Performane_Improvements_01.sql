/* Bug fix duplicate Role name can be entered, so added unique index on roleName */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'listdataschema' and lower(index_name) = 'dbper_listdataschema') then
		drop index dbPer_listDataSchema on listDataSchema;
		create index dbPer_listDataSchema on listDataSchema (project_id);
	else 
		create index dbPer_listDataSchema on listDataSchema (project_id);
    end if;	
	
	if exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'listdatasources' and lower(index_name) = 'dbper_listdatasources') then	
		drop index dbPer_listDataSources on listDataSources;
		create index dbPer_listDataSources on listDataSources (project_id);
	else 
		create index dbPer_listDataSources on listDataSources (project_id);
    end if;	
	
	if exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'listapplications' and lower(index_name) = 'dbper_listapplications') then	
		drop index dbPer_listApplications on listApplications;
        create index dbPer_listApplications on listApplications (project_id,idApp);
	else 
		create index dbPer_listApplications on listApplications (project_id,idApp);
    end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


 