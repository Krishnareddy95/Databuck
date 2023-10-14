/*
   13-Jan-2021 Domain and Projects Standardization in DataBuck (Extending Domain and Project concepts)   
*/
/* New table to allow admin user to define relation between domain -> projects */
create table if not exists domain_to_project (
   row_id               int(11) not null auto_increment primary key,
   domain_id            int(11) not null,
   project_id           int(11) not null,
   is_owner             varchar(1) not null,
   unique key domain_to_project (domain_id, project_id)
) engine=innodb auto_increment=1 default charset=latin1;


/* Add FK domain_id in base objects in databuck */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   /* add domain id in connection table */ 
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'domain_id') then   
      alter table listDataSchema
         add column domain_id int(11) not null default 0;
   end if;
   
   /* add domain id in validation application table */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'domain_id') then   
      alter table listApplications
         add column domain_id int(11) not null default 0;
   end if;   

   /* add domain id in custom rules table */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'domain_id') then   
      alter table listColRules
         add column domain_id int(11) not null default 0;
   end if;   

   /* add domain id in custom rules table */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'domain' and lower(column_name) = 'description') then   
      alter table domain
         add column description varchar(255);
   end if;   
   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

