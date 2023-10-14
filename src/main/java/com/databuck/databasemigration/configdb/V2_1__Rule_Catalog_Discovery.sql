/* 
	07-Jun-2020 : Schema changes included in this file: Rule catalog and auto discovery features (Wells Fargo)
*/

/* New table 'listApplicationsRulesCatalog' - to expand validation into various checks */
create table if not exists listApplicationsRulesCatalog (
   row_id                  int(11) not null auto_increment primary key,
   idApp                   bigint(20) default null,
   rule_reference          int(11) default null,
   rule_code               varchar(255) default null,
   defect_code             varchar(255) default null,
   rule_type               varchar(255) not null,
   column_name             varchar(255) not null,
   rule_category           varchar(255) not null,
   rule_expression         varchar(1000) default null,
   threshold_value         double not null default '0',
   review_comments         varchar(2000) default null,
   review_date datetime    default null,
   review_by varchar(255)  default null
) engine=innodb auto_increment=1 default charset=latin1;

/* Existing table 'listApplications' - add new fields to store approval commnets and status */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Rules catalog changes */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listapplications' and lower(COLUMN_NAME) = 'approve_status') then	
		alter table listApplications
			add column approve_status int;
	end if;	

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listapplications' and lower(COLUMN_NAME) = 'approve_comments') then	
		alter table listApplications
			add column approve_comments varchar(2000);
	end if;		
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listapplications' and lower(COLUMN_NAME) = 'approve_date') then	
		alter table listApplications
			add column approve_date datetime;
	end if;	

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listapplications' and lower(COLUMN_NAME) = 'approve_by') then	
		alter table listApplications
			add column approve_by int;
	end if;		
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* New table 'column_profile_master_table' - to log all columns when each template gets created and then use to auto discover reference rules */
create table if not exists column_profile_master_table (
	row_id			bigint(20) not null auto_increment primary key,
	domain_id		int(11) not null,
	schemaName		varchar(500) default null,	
	idData			int(11) not null,
	template_name	varchar(1000) not null,
	table_name		varchar(10000) not null,	
	column_name		varchar(200)	
) engine=innodb auto_increment=1 default charset=latin1;

/* Existing tables changes for auto discovery */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Existing table 'domain' - Add flag to mark to identify global domain (only one across all rows in domain table) */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'domain' and lower(COLUMN_NAME) = 'is_enterprise_domain') then	
		alter table domain
			add column is_enterprise_domain bit default 0;
	end if;	
	
	/* Existing tables 'listDataAccess' and 'listDataSources' - Refinement done to manage domain for data template */
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdataaccess' and lower(COLUMN_NAME) = 'domain') then	
		alter table listDataAccess
			modify column domain varchar(50);
	end if;		

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdatasources' and lower(COLUMN_NAME) = 'domain_id') then	
		alter table listDataSources
			add column domain_id int(11);
	end if;		
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* Drop down application items for rules catalog approval process */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare nListExists int default 0;
	declare nElementExists int default 0;
	declare nListRowId int default 0;
	declare sListRefText varchar(100) default 'DQ_RULE_CATALOG_STATUS';
	declare sElementRefText varchar(500) default '';
	
	/* List exists or not and get row id of list record */
	select count(*) into nListExists from app_option_list where list_reference = sListRefText;	
	
	if (nListExists < 1) then	
		start transaction;
		insert into app_option_list (list_reference, active) values (sListRefText,1);	
		commit;
	end if;		

	select row_id into nListRowId from app_option_list where list_reference = sListRefText;			

	/* List now surely exists now add elements */
	set sElementRefText = 'NOT APPROVED';		
	start transaction;
	
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Not Approved',0,1,1);
	end if;	
		
	set sElementRefText = 'READY FOR TEST';
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Ready For Test',1,2,1);
	end if;
	
	set sElementRefText = 'APPROVED FOR TEST';
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Approved For Test',1,3,1);
	end if;
	
	set sElementRefText = 'READY FOR EXPORT';
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Ready For Export',1,4,1);
	end if;		
	
	set sElementRefText = 'APPROVED FOR EXPORT';
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Approved For Export',1,5,1);
	end if;	
	
	set sElementRefText = 'REJECTED';
	if not exists (select 1 from app_option_list_elements where element_reference = sElementRefText) then
		insert into app_option_list_elements 
			(elements2app_list, element_reference, element_text, is_default, position, active) 
		values 
			(nListRowId, sElementRefText,'Rejected',1,6,1);
	end if;
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


