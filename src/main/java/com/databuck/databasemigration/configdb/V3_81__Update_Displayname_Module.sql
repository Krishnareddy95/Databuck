drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	start transaction;
	
	
	if exists (select 1 from Module where taskName ='Tasks') then	
		UPDATE Module set displayName ='Run and Schedule' where taskName ='Tasks';
	end if;
	
	if exists (select 1 from Module where taskName ='User Settings') then	
		UPDATE Module set displayName ='Settings' where taskName ='User Settings';
	end if;
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;