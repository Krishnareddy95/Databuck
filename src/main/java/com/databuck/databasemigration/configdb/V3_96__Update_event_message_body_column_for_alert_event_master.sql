drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 	
	
	start transaction;
	
	UPDATE alert_event_master SET event_message_body='Hi {{User}},\nTable {tableName} with validation id {FocusObjectId} and name {Name} has a DTS of {dqi} and {failedChecks} checks failed.' WHERE event_name in ('DQ_Validation_Success', 'DQ_Validation_Failure') ;
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;