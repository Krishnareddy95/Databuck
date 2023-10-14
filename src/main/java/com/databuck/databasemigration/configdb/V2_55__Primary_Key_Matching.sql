/*
	16-Feb-2021 Primary Key Matching	
	New Data Matching feature which allows, to specify more complex match criteria and expressions without linking to data template view (y/n) 
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdmcriteria' and lower(column_name) = 'idleftcolumn') then   
      alter table listDMCriteria
         add column idLeftColumn bigint(20);
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdmcriteria' and lower(column_name) = 'leftsidecolumn') then   
      alter table listDMCriteria
         add column leftSideColumn varchar(500);
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdmcriteria' and lower(column_name) = 'idrightcolumn') then   
      alter table listDMCriteria
         add column idRightColumn bigint(20);
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdmcriteria' and lower(column_name) = 'rightsidecolumn') then   
      alter table listDMCriteria
         add column rightSideColumn varchar(500);
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
   
