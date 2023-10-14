/* --------------- Creating module for Profile Data View-------------------------------- */

/*
	16-Jan-2023 Adding Profile Data View module
*/

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

        declare adminRoleId int default 0;
     	declare nListExists int default 0;
     	declare sListRefText varchar(500) default 'Profile Data View';
     	declare profileDataViewTaskId int default 0;
     	declare profileDataViewAccessExist int default 0;

begin
    /* List exists or not  */
	select count(*) into nListExists from Module where taskName = sListRefText;

	if (nListExists < 1) then
        insert into Module (taskName, createdAt,updatedAt) values (sListRefText,now(),now());
	end if;

	/* Get the  adminRoleId */
	select idRole into adminRoleId from Role where roleName='Admin';

	/* Fetch the task Id for the module */
	select idTask into profileDataViewTaskId from Module where taskName = sListRefText;

	/* check if the access of the module exists */
	select count(*) into profileDataViewAccessExist from RoleModule where idTask=profileDataViewTaskId  and idRole = adminRoleId;

	if (profileDataViewAccessExist < 1) then
    INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,profileDataViewTaskId,'C-R-U-D');
    end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;

