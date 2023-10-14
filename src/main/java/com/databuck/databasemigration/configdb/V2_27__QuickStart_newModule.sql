/* Existing table 'Module' - add new Entry to store Dashboard Module Task Name  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare adminRoleId int default 0;
	declare quickStartTaskExists int default 0;
	declare quickStartTaskName varchar(500) default 'QuickStart';
	declare quickStartTaskId int default 0;
	declare quickStartAccessExist int default 0;
	
	/* List exists or not  */
	select count(*) into quickStartTaskExists from Module where taskName = quickStartTaskName;	
	
	start transaction;
	
	if (quickStartTaskExists < 1) then	
		insert into Module (taskName, createdAt,updatedAt) values (quickStartTaskName,now(),now());	
	end if;	
	
	/* Get the  adminRoleId */
	select idRole into adminRoleId from Role where roleName='Admin';
	
	/* Fetch the task Id for the module */
	select idTask into quickStartTaskId from Module where taskName = quickStartTaskName;
	
	/* check if the access of the module exists */
	select count(*) into quickStartAccessExist from RoleModule where idTask=quickStartTaskId  and idRole = adminRoleId;
	
	if (quickStartAccessExist < 1) then	
		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,quickStartTaskId,'C-R-U-D');
	end if;	
	
	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;