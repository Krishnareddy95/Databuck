/*
	17-Mar-2021 Notification Service Enhancements (Generic, Teladoc and Absa)
	
	Customers want to subcribe to notifications messages in Adhoc manner i.e. specific user may not be link to global and projects email id also, 
	still want to subcribe or get email on specific validation or data template in adhoc way. 	

*/	
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdatasources' and lower(column_name) = 'subcribed_email_id') then	
		alter table listDataSources
			add column subcribed_email_id varchar(1000);
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'subcribed_email_id') then	
		alter table listApplications
			add column subcribed_email_id varchar(1000);
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

