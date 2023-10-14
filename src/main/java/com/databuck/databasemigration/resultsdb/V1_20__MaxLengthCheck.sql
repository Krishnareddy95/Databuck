/* 
	26-Nov-2021 - Max Length check feature changes to results DB.	
*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (
		select 1 from information_schema.columns 
		where table_schema = sSelectedDatabase 
		and lower(table_name) = 'task_progress_status' 
		and lower(column_name) = 'maxlengthcheck') 
	then	
		alter table task_progress_status
			add column maxLengthCheck varchar(1);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;