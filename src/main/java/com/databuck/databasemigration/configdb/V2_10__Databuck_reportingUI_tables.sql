/* --------------- Creating table for Dashboard - Project - connections associations -------------------------------- */
DROP TABLE IF EXISTS `dashboard_project_conn_list`;

CREATE TABLE IF NOT EXISTS `dashboard_project_conn_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `connectionId` bigint(20) NOT NULL,
  `displayName` varchar(200),
  `displayOrder` int,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/* --------------- Creating table for Dashboard - Color grading -------------------------------- */
DROP TABLE IF EXISTS `dashboard_project_color_grade`;

CREATE TABLE IF NOT EXISTS `dashboard_project_color_grade` (
  `gradeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `color` varchar(2000),
  `logic` varchar(2000),
  `color_percentage` Decimal(5,2),
  PRIMARY KEY (`gradeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/* --------------- Creating table for Dashboard - connections - applications associations -------------------------------- */
DROP TABLE IF EXISTS `dashboard_conn_app_list`;

CREATE TABLE IF NOT EXISTS `dashboard_conn_app_list` (
  `conn_app_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `connectionId` bigint(20) NOT NULL,
  `idApp` bigint(20) NOT NULL,
  `datasource` varchar(2500),
  `source` varchar(2500),
  `fileName` varchar(2500),
  PRIMARY KEY (`conn_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/* --------------- Creating table for Dashboard - Check Components list -------------------------------- */
DROP TABLE IF EXISTS `dashboard_check_component_list`;

CREATE TABLE IF NOT EXISTS `dashboard_check_component_list` (
  `componentId` bigint(20) NOT NULL AUTO_INCREMENT,
  `checkName` varchar(2500),
  `description` varchar(2500),
  `component` varchar(2500),
  PRIMARY KEY (`componentId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/* Existing table 'Module' - add new Entry to store 'Dash Configuration' Task Name  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare nListExists int default 0;
	declare sListRefText varchar(500) default 'Dash Configuration';
	
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
