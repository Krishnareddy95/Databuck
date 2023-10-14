drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'dq_jira_msg_queue' and lower(column_name) = 'task_unique_id') then
        /* Add column */
        ALTER table DQ_JIRA_MSG_QUEUE add column task_unique_id varchar(255) default null ;
    end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;