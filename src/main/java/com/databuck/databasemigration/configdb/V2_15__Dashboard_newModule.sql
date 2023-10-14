/* Existing table 'Module' - add new Entry to store Dashboard Module Task Name  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare nListExists int default 0;
	declare sListRefText varchar(500) default 'Dashboard';
	
	/* List exists or not  */
	select count(*) into nListExists from Module where taskName = sListRefText;	
	start transaction;
	
	if (nListExists < 1) then	
		insert into Module (taskName, createdAt,updatedAt) values (sListRefText,now(),now());	
	end if;		
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;