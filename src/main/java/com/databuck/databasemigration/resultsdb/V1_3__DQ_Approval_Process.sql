/* 
   Schema changes for data quality approval process (AbbVie 17-Apr-2020) in databuck results DB 
*/
create table if not exists data_quality_approval_log (                      
  row_id               int(11) not null primary key auto_increment,         
  idApp                bigint(20) not null,                                 
  date                 datetime not null,                                 
  run                  int(11) not null,
  action_type          varchar(1) not null,               
  action_state         int(11),                                    
  action_comments      varchar(2000),                                       
  action_date          datetime,
  action_by            bigint(20),                                          
  entry_date           date not null
) engine=innodb auto_increment=1 default charset=latin1;    
                                                                         
/* Application options list (generic design) will be used for approval process as well in future for any app drop down list elements */
create table if not exists app_option_list (
  row_id               int(11) not null primary key auto_increment,
  list_reference       varchar(255) not null unique,
  active               bit(1) not null default 1
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

/* Insert all possible drop down list as needed for this approval process */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare nListExists int default 0;
   declare nElementExists int default 0;
   declare nListRowId int default 0;
   declare sListReference varchar(500) default '';   

   /* 001 => Status values of approval for last run of validation application */
   set sListReference = 'DQ_APPROVAL_PROCESS_STATUS';
   
   select count(*) into nListExists from app_option_list where list_reference = sListReference;   
   
   if (nListExists < 1) then   
      start transaction;
      insert into app_option_list (list_reference, active) values (sListReference,1);   
      commit;
   end if;      

   select row_id into nListRowId from app_option_list where list_reference = sListReference;         
   
   start transaction;   

   if not exists (select 1 from app_option_list_elements where element_reference = 'NOT_STARTED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'NOT_STARTED','Not Started',1,1,1);
   end if;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'REVIEWED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'REVIEWED','Reviewed',0,2,1);
   end if;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'APPROVED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'APPROVED','Approved',0,3,1);
   end if;

   commit;

   /* 002 => Review comments status values for last run of validation application */
   set sListReference = 'DQ_REVIEW_STATUS';
   select count(*) into nListExists from app_option_list where list_reference = sListReference;   
   
   if (nListExists < 1) then   
      start transaction;
      insert into app_option_list (list_reference, active) values (sListReference,1);   
      commit;
   end if;      

   select row_id into nListRowId from app_option_list where list_reference = sListReference;
   
   start transaction;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'RESEARCH' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'RESEARCH','Research',1,1,1);
   end if;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'CAN_PROCEED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'CAN_PROCEED','Can Proceed',0,2,1);
   end if;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'NEEDS_TO_STOP' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'NEEDS_TO_STOP','Needs to Stop',0,3,1);
   end if;   

   /* 003 => Approval comments status values for last run of validation application */
   set sListReference = 'DQ_APPROVE_STATUS';
   select count(*) into nListExists from app_option_list where list_reference = sListReference;   
   
   if (nListExists < 1) then   
      start transaction;
      insert into app_option_list (list_reference, active) values (sListReference,1);   
      commit;
   end if;      

   select row_id into nListRowId from app_option_list where list_reference = sListReference;
   
   start transaction;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'APPROVED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'APPROVED','Approved',0,1,1);
   end if;
   
   if not exists (select 1 from app_option_list_elements where element_reference = 'NOT_APPROVED' and elements2app_list = nListRowId) then
      insert into app_option_list_elements 
         (elements2app_list, element_reference, element_text, is_default, position, active) 
      values 
         (nListRowId, 'NOT_APPROVED','Not Approved',1,2,1);
   end if;
   
   commit;
   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

