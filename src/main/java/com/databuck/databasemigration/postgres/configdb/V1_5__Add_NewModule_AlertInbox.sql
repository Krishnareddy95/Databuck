/* --------------- Creating module for Alert Inbox-------------------------------- */

/*
	02-Nov-2022 Adding Alert Inbox module
*/
drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());
     declare adminRoleId int default 0;
     declare nListExists int default 0;
     declare sListRefText varchar(500) default 'Alert Inbox';
     declare alertInboxTaskId int default 0;
     declare alertInboxAccessExist int default 0;

begin
    /* List exists or not  */
	select count(*) into nListExists from Module where taskName = sListRefText;

	if (nListExists < 1) then
        insert into Module (taskName, createdAt,updatedAt) values (sListRefText,now(),now());
	end if;

	/* Get the  adminRoleId */
	select idRole into adminRoleId from Role where roleName='Admin';

	/* Fetch the task Id for the module */
	select idTask into alertInboxTaskId from Module where taskName = sListRefText;

	/* check if the access of the module exists */
	select count(*) into alertInboxAccessExist from RoleModule where idTask=alertInboxTaskId  and idRole = adminRoleId;

	if (alertInboxAccessExist < 1) then
    		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,alertInboxTaskId,'C-R-U-D');
    end if;
end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;


