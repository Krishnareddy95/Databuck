drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	UPDATE listSchedule ls, domain_to_project dp SET ls.domain_id = dp.domain_id where ls.project_id = dp.project_id and ls.domain_id =0;  

 commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;