/*
   Add new column approved_by_user to listApplications to store the name of user who approved the rule catalog
*/   
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   /* Add column approved_by_user */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'approver_name') then   
      alter table listApplications add column approver_name varchar(2500) default null;
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


