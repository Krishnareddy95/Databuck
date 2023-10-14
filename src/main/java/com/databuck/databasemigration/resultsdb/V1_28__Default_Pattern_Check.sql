/* --------------- Creating table for Default PatternCheck -------------------------------- */
CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Unmatched_Default_Pattern_Data` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` int(11) DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `Run` bigint(20) DEFAULT NULL,
  `Col_Name` text,
  `Total_Records` bigint(20) DEFAULT NULL,
  `Total_Failed_Records` bigint(20) DEFAULT NULL,
  `Total_Matched_Records` bigint(20) DEFAULT NULL,
  `Patterns_List` text,
  `New_Pattern` varchar(5) DEFAULT NULL,
  `FailedRecords_Percentage` double DEFAULT NULL, 
  `Pattern_Threshold` double DEFAULT NULL,
  `Status` varchar(10),
  `forgot_run_enabled` varchar(10) DEFAULT 'N',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* --------------- Adding columns for column_profile_master_table, task_progress_status - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_master_table' and lower(COLUMN_NAME) = 'Default_Patterns') then	
		alter table column_profile_master_table add column `Default_Patterns` text DEFAULT null;
	end if;
	
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'task_progress_status' and lower(COLUMN_NAME) = 'defaultPatternCheck') then	
		alter table task_progress_status add column `defaultPatternCheck` varchar(10);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;