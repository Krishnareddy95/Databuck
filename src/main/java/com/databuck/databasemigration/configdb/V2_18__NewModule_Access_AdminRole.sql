/* Provide Access of all the new Modules to Admin Role  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare adminRoleId int default 0;
	declare globalRuleTaskId int default 0;
	declare dashConfigTaskId int default 0;
	declare dashboardTaskId int default 0;
	declare appSettingsTaskId int default 0;
	
	declare globalRuleAccessExist int default 0;
	declare dashConfigAccessExist int default 0;
	declare dashboardAccessExist int default 0;
	declare appSettingsAccessExist int default 0;
	
	declare globalRuleTaskName varchar(500) default 'Global Rule';
	declare dashConfigTaskName varchar(500) default 'Dash Configuration';
	declare dashboardTaskName varchar(500) default 'Dashboard';
	declare appSettingsTaskName varchar(500) default 'Application Settings';
	
	/* Get the  adminRoleId */
	select idRole into adminRoleId from Role where roleName='Admin';
	
	/* Fetch the task Id for the modules */
	select idTask into globalRuleTaskId from Module where taskName = globalRuleTaskName;	
	select idTask into dashConfigTaskId from Module where taskName = dashConfigTaskName;
	select idTask into dashboardTaskId from Module where taskName = dashboardTaskName;
	select idTask into appSettingsTaskId from Module where taskName = appSettingsTaskName;
	
	select count(*) into globalRuleAccessExist from RoleModule where idTask=globalRuleTaskId  and idRole = adminRoleId;
	select count(*) into dashConfigAccessExist from RoleModule where idTask=dashConfigTaskId  and idRole = adminRoleId;
	select count(*) into dashboardAccessExist from RoleModule where idTask=dashboardTaskId  and idRole = adminRoleId;
	select count(*) into appSettingsAccessExist from RoleModule where idTask=appSettingsTaskId  and idRole = adminRoleId;

	start transaction;
	
	if (globalRuleAccessExist < 1) then	
		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,globalRuleTaskId,'C-R-U-D');
	end if;	
	
	if (dashConfigAccessExist < 1) then	
		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,dashConfigTaskId,'C-R-U-D');
	end if;		
	
	if (dashboardAccessExist < 1) then	
		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,dashboardTaskId,'C-R-U-D');
	end if;		
	
	if (appSettingsAccessExist < 1) then	
		INSERT INTO RoleModule (idRole,idTask,accessControl) VALUES (adminRoleId,appSettingsTaskId,'C-R-U-D');
	end if;		
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;