/*
   01-May-2021 Login Group Enhancements to implement approval priviledges based on login group   
*/   
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'login_group' and lower(column_name) = 'is_approver') then   
      alter table login_group
         add column is_approver bit default 0;
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


