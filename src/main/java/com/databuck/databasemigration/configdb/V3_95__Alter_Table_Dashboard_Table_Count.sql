/* Existing table 'dashboard_table_count' - Modify column hours_saved  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Rules catalog changes - rule name needed apart from column name for non column based features */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dashboard_table_count' and lower(column_name) = 'hours_saved') then	
			ALTER TABLE `dashboard_table_count` 
			CHANGE COLUMN `hours_saved` `hours_saved` VARCHAR(50) NULL DEFAULT NULL ;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
