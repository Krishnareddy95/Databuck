/* SOR or sub domain new table as part of security matrix */
create table if not exists listOfSORs (
   idSORs                  int(11) not null primary key auto_increment,
   sor_name                varchar(100),
   unique key sor_name (sor_name)      
) engine=innodb auto_increment=1 default charset=latin1;

/* Bug fix duplicate Role name can be entered, so added unique index on roleName */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 
   
   /* Add SOR and domain as security combination applicable to LDAP group */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'databuck_security_matrix' and lower(column_name) = 'idsors') then   
      alter table databuck_security_matrix
         add column idSORs int(11) not null default 0;
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'databuck_security_matrix' and lower(column_name) = 'domainid') then   
      alter table databuck_security_matrix
         add column domainId int(11) not null default 0;
   end if;   
   
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'idsors') then   
      alter table listDataSchema
         add column idSORs int(11);
   end if;      
   
   if not exists (select 1 from information_schema.statistics where table_schema = sSelectedDatabase and  lower(table_name) = 'databuck_security_matrix' and lower(index_name) = 'databuck_security_matrix') then   
      create unique index databuck_security_matrix on databuck_security_matrix (ldap_group_name, idRole, domainId, idSORs);
   else 
      alter table databuck_security_matrix 
         drop index databuck_security_matrix, 
         add unique key databuck_security_matrix (ldap_group_name, idRole, domainId, idSORs);
   end if;      
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;



