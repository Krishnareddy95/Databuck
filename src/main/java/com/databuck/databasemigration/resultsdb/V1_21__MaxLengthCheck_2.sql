/* 
	06-Dec-2021 - Max Length check feature changes to results DB.
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'task_progress_status' and lower(column_name) = 'maxlengthcheck') then
            ALTER TABLE task_progress_status MODIFY maxLengthCheck varchar(50) default null;
    end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;