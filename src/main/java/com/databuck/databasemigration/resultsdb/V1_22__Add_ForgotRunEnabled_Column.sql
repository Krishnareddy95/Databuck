drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transactionset_sum_a1' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Transactionset_sum_A1 add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transactionset_sum_dgroup' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Transactionset_sum_dgroup add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_nullcheck_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_NullCheck_Summary add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_record_anomaly' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Record_Anomaly add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_default_value' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_default_value add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_baddata' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_badData add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_length_check' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Length_Check add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_data_drift' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_DATA_DRIFT add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_data_drift_count_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_data_drift_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_DATA_DRIFT_SUMMARY add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_identity' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Transaction_Detail_Identity add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_detail_all' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Transaction_Detail_All add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_transaction_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Transaction_Summary add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_unmatched_pattern_data' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Unmatched_Pattern_Data add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_rules' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Rules add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_timeliness_check' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_timeliness_check add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_column_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_Column_Summary add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_daterule_failedrecords' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_DateRule_FailedRecords add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_daterule_summary' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_DateRule_Summary add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_globalrules' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_GlobalRules add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_history_anomaly' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table DATA_QUALITY_History_Anomaly add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'data_quality_sql_rules' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table data_quality_sql_rules add column forgot_run_enabled varchar(10) default 'N';
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'processData' and lower(column_name) = 'forgot_run_enabled') then
		/* Add column */
		ALTER table processData add column forgot_run_enabled varchar(10) default 'N';
	end if;

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

