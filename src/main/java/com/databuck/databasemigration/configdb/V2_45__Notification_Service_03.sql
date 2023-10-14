/*
   19-Jan-2021 Next stage of notification service - allow user to edit message subject, body and make copy of other version
*/

/* Add additional tables for stage 2 development */
create table if not exists notification_tags_master (
   row_id               int(11) not null auto_increment primary key,
   tag_id               varchar(100) not null,
   tag_description      varchar(255) default '',
   active               bit not null default 1,
   unique key unique_notification_tag (tag_id)
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists notification_applicable_tags (
   row_id               int(11) not null auto_increment primary key,
   topic_row_id         int(11) not null,
   tag_row_id           int(11) not null,
   active               bit not null default 1,
   unique key unique_topic_tag_mapping (topic_row_id, tag_row_id)
) engine=innodb auto_increment=1 default charset=latin1;

/* Add additional fields for stage 2 development */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
   declare sSelectedDatabase varchar(100) default '';   
   select database() into sSelectedDatabase; 

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'active') then   
      alter table notification_topics
         add column active bit default 1;
   end if;

   if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'notification_topics' and lower(column_name) = 'managed_by') then   
      alter table notification_topics
         add column managed_by tinyint default 0;
   end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

