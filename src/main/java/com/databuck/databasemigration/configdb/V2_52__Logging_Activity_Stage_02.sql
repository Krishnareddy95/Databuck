/* Bug fix User Name can be quite long */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'logging_activity' and lower(column_name) = 'user_name') then   
      alter table logging_activity
         modify user_name varchar(500) not null;
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

