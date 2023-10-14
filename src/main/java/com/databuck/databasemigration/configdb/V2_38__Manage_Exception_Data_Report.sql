/*
   17-Dec-2020 Exception Data Report 
   (New Feature - Failed data for all DQ features to be combined, send as e-mail attachment to operaional group)
   
   New table needed to club more than one validation checks as group but these will not be run but 
   just their already run CSV files are combined to consolidate all failed records as one XLS/CSV    
*/   
create table if not exists exception_data_report (
   row_id               int(11) not null auto_increment primary key,
   report_id            int(11) not null,
   name                 varchar(255),
   description          varchar(255),   
   data_frequency       tinyint,
   project_id           int(11) not null,
   created_by           int(11) not null,
   created_date         datetime not null,
   modified_by          int(11) not null,
   modified_date        datetime not null,      
   unique key unique_report_name (project_id, name)
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists exception_data_report_apps (
   row_id             int(11) not null auto_increment primary key,
   report_row_id      int(11) not null,
   app_row_id         bigint(20) not null,
   unique key unique_report_app (report_row_id, app_row_id)
) engine=innodb auto_increment=1 default charset=latin1;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 
   
   /* select module and role row ids into mysql session variables */
   set @nDataConnRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Connection');
   set @nDataTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Data Template');
   set @nExtnTmplRowId     = (select idTask from Module where upper(trim(taskName)) = 'Extend Template & Rule');
   set @nValCheckRowId     = (select idTask from Module where upper(trim(taskName)) = 'Validation Check');
   set @nTasksRowId        = (select idTask from Module where upper(trim(taskName)) = 'Tasks');
   set @nResultsRowId      = (select idTask from Module where upper(trim(taskName)) = 'Results');
   set @nUserSettingsRowId = (select idTask from Module where upper(trim(taskName)) = 'User Settings');
   set @nGlobalRuleRowId   = (select idTask from Module where upper(trim(taskName)) = 'Global Rule');
   set @nDashConfigRowId   = (select idTask from Module where upper(trim(taskName)) = 'Dash Configuration');
   set @nDashboardRowId    = (select idTask from Module where upper(trim(taskName)) = 'Dashboard');
   set @nAppSettingsRowId  = (select idTask from Module where upper(trim(taskName)) = 'Application Settings');

   set @nAdminRoleRowId = (select case when (select count(*) from Role where upper(trim(roleName)) = 'ADMIN') > 0 then (select idRole from Role where upper(trim(roleName)) = 'ADMIN') else -1 end as RoleRowId); 
   set @sComponentName = 'Exception_Data_Report'; 
   set @nComponentRowId = (select row_id from component where component_name = @sComponentName);

   start transaction;
   
   if (@nComponentRowId is null) then
      insert into component (component_name,component_title,component_type,module_row_id,http_url)   values (@sComponentName, 'Exception Data Report', 0, @nUserSettingsRowId, '/ManageExceptionDataReport');
      set @nComponentRowId = (select row_id from component where component_name = @sComponentName);
      
      /* insert all access rights for admin user */
      insert into component_access (role_row_id, component_row_id) values (@nAdminRoleRowId, @nComponentRowId);       
   end if;
   
     commit;   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

