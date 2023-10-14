/* Global thresholds base table */
create table if not exists listGlobalThresholds (
   idGlobalThreshold       int(11) not null primary key auto_increment,
   domainId						int(11) not null,   
   globalColumnName        varchar(200) not null,
   description             varchar(1000),
   nullCountThreshold      double not null default 0,
   numericalThreshold      double not null default 0,
   stringStatThreshold     double not null default 0,
   dataDriftThreshold      double not null default 0,
   recordAnomalyThreshold  double not null default 0,
   unique key threshold_name (domainId, globalColumnName)      
) engine=innodb auto_increment=1 default charset=latin1;

/* Global thresholds which gets selected by user during template creation and gets linked to data template */
create table if not exists listGlobalThresholdsSelected (
   idGlobalThresholdSelected  int(11) not null primary key auto_increment,
   idGlobalThreshold          int not null,                              
   idData                     int(11) not null,
   idColumn                   bigint(20) not null,
   nullCountThreshold         double not null,
   numericalThreshold         double not null,
   stringStatThreshold        double not null,
   dataDriftThreshold         double not null,
   recordAnomalyThreshold     double not null   
) engine=innodb auto_increment=1 default charset=latin1;

/* Application options list (generic design) will be used for global thresholds as well in future for any app drop down list elements */
create table if not exists app_option_list (
  row_id         int(11) not null primary key auto_increment,
  list_reference varchar(255) not null unique,
  active         bit(1) not null default 1
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists app_option_list_elements (
  row_id int(11)          not null primary key auto_increment,
  elements2app_list       varchar(255) not null,
  element_reference       varchar(255) not null,
  element_text            varchar(500) not null,
  is_default              bit(1) not null default 0,
  position                int not null,
  active                  bit(1) not null default 1,
  unique key element_reference (elements2app_list, element_reference)      
) engine=innodb auto_increment=1 default charset=latin1;


/* Add how to apply thresholds option column to use in validation page in listApplications table */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Check if column already exists, if not then only add it */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(column_name) = 'thresholdsapplyoption') then	
		alter table listApplications
			add column thresholdsApplyOption int(11) not null default 0;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* Create generic app option values table and add values for global threshold solution */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare nListExists int default 0;
	declare nElementExists int default 0;
	declare nListRowId int default 0;
	
	/* List exists or not and get row id of list record */
	select count(*) into nListExists from app_option_list where list_reference = 'GLOBAL_THRESHOLDS_OPTION';	
	
	if (nListExists < 1) then	
		start transaction;
		insert into app_option_list (list_reference, active) values ('GLOBAL_THRESHOLDS_OPTION',1);	
		commit;
	end if;		

	select row_id into nListRowId from app_option_list where list_reference = 'GLOBAL_THRESHOLDS_OPTION';			

	/* List now surely exists now elements add/update */
	start transaction;
	
	if not exists (select 1 from app_option_list_elements where element_reference = 'USE_BASE_VALUE') then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, 'USE_BASE_VALUE','Use base threshold value from data template',0,1,1);
	end if;
	
	if not exists (select 1 from app_option_list_elements where element_reference = 'USE_GLOBAL_THRESHOLD_VALUE') then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, 'USE_GLOBAL_THRESHOLD_VALUE','Use value entered in global threshold page',1,2,1);
	end if;
	
	if not exists (select 1 from app_option_list_elements where element_reference = 'USE_USER_REJECTED_VALUE') then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, 'USE_USER_REJECTED_VALUE','Use value rejected from dashboard page',0,3,1);
	end if;
	
	if not exists (select 1 from app_option_list_elements where element_reference = 'USE_MOVING_AVERAGE_VALUE') then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, 'USE_MOVING_AVERAGE_VALUE','Use value as auto adjusting moving average', 0,4,1);
	end if;
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

