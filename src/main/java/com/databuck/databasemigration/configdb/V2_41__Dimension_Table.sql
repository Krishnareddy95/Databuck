/* New table to allow admin user to define Dimensions */
create table if not exists dimension (
   idDimension               int(11) not null auto_increment primary key,
   dimensionName        varchar(45) not null,
   unique key domain_to_project (idDimension, dimensionName)
) engine=innodb auto_increment=1 default charset=latin1;

/* Add FK domensionId in base objects in databuck */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare dimensionExists int default 0;
   declare sSelectedDatabase varchar(100) default ''; 
   declare defaultDimensionName varchar(500) default 'undefined';
   select database() into sSelectedDatabase; 
   
   
	
   /* add domension id in custom rules table */
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listcolrules' and lower(column_name) = 'domensionId') then   
      alter table listColRules
         add column domensionId int(11) not null default 1;
   end if;  
   
   /* List exists or not  */
	select count(*) into dimensionExists from dimension;	
	
	start transaction;
	
	if (dimensionExists < 1) then	
		insert into dimension (dimensionName) values (defaultDimensionName);	
	end if;
	
	commit;

   
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
