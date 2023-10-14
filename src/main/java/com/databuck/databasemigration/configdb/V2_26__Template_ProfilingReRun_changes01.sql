/* --------------- Updating profilingReRunEnabled data to existing templates  -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	/* For all the existing templates set profilingReRunEnabled='N' */
	update listDataSources set profilingReRunEnabled='N';
	/* For all the existing active templates for which profiling is enabled make profilingReRunEnabled='Y' */
	update listDataSources set profilingReRunEnabled='Y' where idData in (select idData from listDataAccess) and active='Yes' and profilingEnabled='Y' and dataLocation not in ('File Management','Kafka');
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;