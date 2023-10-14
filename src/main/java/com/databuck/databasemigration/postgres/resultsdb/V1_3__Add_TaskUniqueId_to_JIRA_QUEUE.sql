drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

	if not exists (select 1 from information_schema.columns where table_schema = '${resultsdbSchemaName}' and table_catalog=sSelectedDatabase  and lower(table_name) = 'dq_jira_msg_queue' and lower(column_name) = 'task_unique_id') then
        /* Add column */
        ALTER table DQ_JIRA_MSG_QUEUE add column task_unique_id varchar(255) default null ;
    end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;