/*
 * Adding new events inside alert_event_master
 */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	if exists (select 1 from alert_event_master where event_name='DQ_Validation_Success') then
	    update alert_event_master set event_message_subject='Validation -[{FocusObjectId}] execution status',event_message_body='Validation with Id - [{FocusObjectId}] and name -[{Name}] ran successfully.\nData Quality Score:{dqi}.' where event_name='DQ_Validation_Success';
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
