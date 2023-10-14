/*
   2-Feb-2021 - Defect (new table for wells fargo) and Dimention (concept to group DataBuck features) Relations
*/


create table if not exists defect_codes (
   row_id               int(11) not null auto_increment primary key,
   defect_code          varchar(50),
   defect_description   varchar(255),
   dimension_id         int(11) not null,
   unique key unique_defect_code (defect_code),
   unique key unique_defect_dimension (defect_code, dimension_id)
) engine=innodb auto_increment=1 default charset=latin1;

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolglobalrules' and lower(column_name) = 'dimension_id') then   
      alter table listColGlobalRules
         add column dimension_id int(11) not null;
   end if;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

