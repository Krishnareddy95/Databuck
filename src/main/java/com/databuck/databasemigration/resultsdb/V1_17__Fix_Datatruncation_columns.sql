/* --------------- Changing datatype of columns having data truncation issue -------------------------------- */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_column_summary' and lower(COLUMN_NAME) = 'colname') then	
		alter table DATA_QUALITY_Column_Summary
			modify column ColName text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_data_drift' and lower(COLUMN_NAME) = 'dgroupval') then	
		alter table DATA_QUALITY_DATA_DRIFT
			modify column dGroupVal text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_data_drift' and lower(COLUMN_NAME) = 'dgroupcol') then	
		alter table DATA_QUALITY_DATA_DRIFT
			modify column dGroupCol text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_data_drift_summary' and lower(COLUMN_NAME) = 'dgroupval') then	
		alter table DATA_QUALITY_DATA_DRIFT_SUMMARY
			modify column dGroupVal text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_data_drift_summary' and lower(COLUMN_NAME) = 'dgroupcol') then	
		alter table DATA_QUALITY_DATA_DRIFT_SUMMARY
			modify column dGroupCol text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_daterule_summary' and lower(COLUMN_NAME) = 'datefield') then	
		alter table DATA_QUALITY_DateRule_Summary
			modify column DateField text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_globalrules' and lower(COLUMN_NAME) = 'rulename') then	
		alter table DATA_QUALITY_GlobalRules
			modify column ruleName text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_history_anomaly' and lower(COLUMN_NAME) = 'dgroupval') then	
		alter table DATA_QUALITY_History_Anomaly
			modify column dGroupVal text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_history_anomaly' and lower(COLUMN_NAME) = 'dgroupcol') then	
		alter table DATA_QUALITY_History_Anomaly
			modify column dGroupCol text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_badData' and lower(COLUMN_NAME) = 'colname') then	
		alter table DATA_QUALITY_badData
			modify column ColName text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_rules' and lower(COLUMN_NAME) = 'rulename') then	
		alter table DATA_QUALITY_Rules
			modify column ruleName text default null;
	end if;	
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_default_value' and lower(COLUMN_NAME) = 'colname') then	
		alter table DATA_QUALITY_default_value
			modify column ColName text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_default_value' and lower(COLUMN_NAME) = 'default_value') then	
		alter table DATA_QUALITY_default_value
			modify column Default_Value text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_sql_rules' and lower(COLUMN_NAME) = 'rulename') then	
		alter table data_quality_sql_rules
			modify column ruleName text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_historic_dashboard' and lower(COLUMN_NAME) = 'validationcheckname') then	
		alter table data_quality_historic_dashboard
			modify column validationcheckname text default null;
	end if;
	
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'dashboard_summary' and lower(COLUMN_NAME) = 'key_metric_3') then	
		alter table DashBoard_Summary
			modify column Key_Metric_3 text default null;
	end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
