/* --------------- Adding columns for DashBoard_Summary - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'DashBoard_Summary' and lower(COLUMN_NAME) = 'Key_Metric_3') then	
		alter table DashBoard_Summary
			add column Key_Metric_3 varchar(1000) default null after Key_Metric_2;
	end if;

	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;