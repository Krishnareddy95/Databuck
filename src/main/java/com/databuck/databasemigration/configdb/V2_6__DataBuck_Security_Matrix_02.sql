/* Syncronization to MapR Branch domainId to be replaced by project_id */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 
   
   /* Correction which will affect only main branch recent changes to Java */
   if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listofsors' and lower(column_name) = 'sor_name') then   
      alter table listOfSORs
         drop column sor_name;
   end if;
   
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listofsors' and lower(column_name) = 'sorName') then   
      alter table listOfSORs
         add column sorName varchar(100);
   end if;   
   
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listofsors' and lower(column_name) = 'projectid') then   
      alter table listOfSORs
         add column projectId int(11) not null;
   end if;   
  
   if not exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'listofsors' and lower(index_name) = 'sorname') then   
      create unique index sorName on listOfSORs (sorName);
	end if;      
   
   /* Add SOR and idProject as security combination applicable to LDAP group */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'databuck_security_matrix' and lower(column_name) = 'idproject') then   
      alter table databuck_security_matrix
         add column idProject int(11);
   end if;
   
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'databuck_security_matrix' and lower(column_name) = 'iduser') then   
      alter table databuck_security_matrix
         add column idUser int(11);
   end if;
 
   if not exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'databuck_security_matrix' and lower(index_name) = 'databuck_security_matrix') then   
      create unique index databuck_security_matrix on databuck_security_matrix (ldap_group_name, idRole, idProject, idSORs);
   else 
      alter table databuck_security_matrix 
         drop index databuck_security_matrix, 
         add unique key databuck_security_matrix (ldap_group_name, idRole, idProject, idSORs);
   end if;      
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;




