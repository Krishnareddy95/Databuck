/*
   04-June-2021 Executive Summary enhancements stage 2 (based on requirements coming from Absa customer)
*/   

/* Master table to store list of data domain (each customer may have different set, use active field manage what ever applicable to specific customer */


create table if not exists data_domain (
   row_id               int(11) not null auto_increment primary key,
	name						varchar(100) not null,	
	active 					bit not null default 1, 
   unique key unique_data_domain (name)
) engine=innodb auto_increment=1 default charset=latin1;

/* Initial values as applicable to Absa customer */
set @sDummyDataDomain = 'do_not_use';
set @nDefaultDataDomainRowId = 0;

start transaction;
insert into data_domain (name) values (@sDummyDataDomain);

set @sNewDataDomain = 'Customer';

set @sNewDataDomain = (select case when not exists (select 1 from data_domain where upper(name) = upper(@sNewDataDomain)) then @sNewDataDomain else '-1' end);
insert into data_domain (name) select @sNewDataDomain from data_domain where @sNewDataDomain <> '-1' limit 1;

set @sNewDataDomain = 'Product';

set @sNewDataDomain = (select case when not exists (select 1 from data_domain where upper(name) = upper(@sNewDataDomain)) then @sNewDataDomain else '-1' end);
insert into data_domain (name) select @sNewDataDomain from data_domain where @sNewDataDomain <> '-1' limit 1;

set @sNewDataDomain = 'Transaction';

set @sNewDataDomain = (select case when not exists (select 1 from data_domain where upper(name) = upper(@sNewDataDomain)) then @sNewDataDomain else '-1' end);
insert into data_domain (name) select @sNewDataDomain from data_domain where @sNewDataDomain <> '-1' limit 1;

delete from data_domain where name = @sDummyDataDomain;

commit;

set @sNewDataDomain = 'Customer';
set @nDefaultDataDomainRowId = (select row_id from data_domain where name = @sNewDataDomain);

/* Add FK data domain into list application */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'data_domain_id') then         
      alter table listApplications
         add column data_domain_id tinyint;
   end if;
   
   start transaction;   
   
   update listApplications set data_domain_id = @nDefaultDataDomainRowId where data_domain_id is null;
   
   commit;   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


