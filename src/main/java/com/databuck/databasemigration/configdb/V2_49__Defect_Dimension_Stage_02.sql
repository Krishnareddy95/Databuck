/*
   2-Feb-2021 - Adding DimensionId Column to RuleCatalaog
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'dimension_id') then   
      alter table listApplicationsRulesCatalog
         add column dimension_id int(11) default null;
   end if;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;