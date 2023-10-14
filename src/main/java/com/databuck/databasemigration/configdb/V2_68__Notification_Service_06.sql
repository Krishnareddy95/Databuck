/*
   23-March-2021 Notification Service Enhancements   
   
   We are planning to use notification service as standard mechanism to publish DataBuck events happening to external world/applications
   Generally this portion should be standard for all customers, but occasions may be there to customize as per different different customer 
   requirements.  We may use already added field cusmization_context and achieve customer specific diversions in interface data
*/   
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'is_publish_externally') then   
      alter table notification_topics
         add column is_publish_externally bit default 1;
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'publish_url_1') then   
      alter table notification_topics
         add column publish_url_1 varchar(1000) default null;
   end if;   
   
   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'publish_url_2') then   
      alter table notification_topics
         add column publish_url_2 varchar(1000) default null;
   end if;      

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;


