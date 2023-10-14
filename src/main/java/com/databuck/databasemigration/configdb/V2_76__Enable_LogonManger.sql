/*
   Add new column to listDataSchema table, to get database connection details from LogonManager 
*/   
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   /* Add column kmsAuthDisabled with default value 'Y', if value is 'N' connection details comes from logonManager */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'kmsauthdisabled') then   
      alter table listDataSchema add column kmsAuthDisabled varchar(10) default 'Y';
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


