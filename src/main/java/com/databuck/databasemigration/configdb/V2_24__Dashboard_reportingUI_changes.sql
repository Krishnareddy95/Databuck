/* --------------- Adding new columns to dashboard_check_component_list - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'dashboard_check_component_list' and lower(COLUMN_NAME) = 'entity_name') then	
		alter table dashboard_check_component_list
			add column entity_name varchar(2500);
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'dashboard_check_component_list' and lower(COLUMN_NAME) = 'technical_name') then	
		alter table dashboard_check_component_list
			add column technical_name varchar(2500);
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'dashboard_check_component_list' and lower(COLUMN_NAME) = 'technical_check_value') then	
		alter table dashboard_check_component_list
			add column technical_check_value varchar(2500);
	end if;
	
	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'dashboard_check_component_list' and lower(COLUMN_NAME) = 'technical_result_name') then	
		alter table dashboard_check_component_list
			add column technical_result_name varchar(2500);
	end if;
	
	start transaction;
	
	/* Delete old data from table */
	delete from dashboard_check_component_list;
	
	/* Insert new data to table */
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Null Check','Essential Check','Check number of Nulls values in a column','listApplications','nonNullCheck','Y','DQ_Completeness');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('MicroSegment Based Null Check','Essential Check','Check number of Nulls values in a column per microsegment','listApplications','dGroupNullCheck','Y','DQ_Completeness');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Default Value check','Essential Check','Check if only Default values specified are present in column','listApplications','defaultCheck','Y','DQ_DefaultCheck');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Length Check','Essential Check','Check if column data is only of specified length','listApplications','lengthCheck','Y','DQ_LengthCheck');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Bad Data Check','Essential Check','Check if column has bad data','listApplications','badData','Y','DQ_Bad_Data');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Date Rule Check','Essential Check','Perform Date comparision checks ','listApplications','dateRuleCheck','Y','DQ_DateRuleCheck');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Microsegment Based Date Rule Check','Essential Check','Perform Date comparision checks per microsegment','listApplications','dGroupDateRuleCheck','Y','DQ_DateRuleCheck');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Pattern Check','Essential Check','Check if column data is matching the specified pattern','listApplications','patternCheck','Y','DQ_Pattern_Data');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Duplicate Checks ','Essential Check','Check if column has duplicate data either primary keys or selected fields','listDFTranRule','dupRow','Y','DQ_Uniqueness -Primary Keys, DQ_Uniqueness -Seleted Fields');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Custom Rule Checks ','Essential Check','Execute and validate custom rules','listApplications','applyRules','Y','DQ_Sql_Rule,DQ_Rules');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Record Count Check','Essential Check','Check if Record count of data has anomaly','listApplications','recordCountAnomaly','Y','DQ_Record Count Fingerprint');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Microsegment Based Record Count Check','Essential Check','Check if Record count of microsegment data has anomaly','listApplications','keyGroupRecordCountAnomaly','Y','DQ_Record Count Fingerprint');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Timeliness Check','Advanced Check','Perform timeliness check','listApplications','timelinessKeyCheck','Y','DQ_Timeliness');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Record Anomaly – Current Batch and Historical','Advanced Check','Check if column data has anomaly','listApplications','recordAnomalyCheck','Y','DQ_Record Anomaly');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Trend Check / Numerical Fingerprint ','Advanced Check','Numerical column distribution check','listApplications','numericalStatCheck','Y','DQ_Numerical Field Fingerprint');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Data Drift Check ','Advanced Check','Check differences in Unique values of column','listApplications','dataDriftCheck','Y','DQ_Data Drift');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Microsegment Based Data Drift Check','Advanced Check','Check differences in Unique values in a column microsegment','listApplications','dGroupDataDriftCheck','Y','DQ_Data Drift');
	insert into dashboard_check_component_list (checkName,component,description,entity_name,technical_name,technical_check_value,technical_result_name) values('Derived columns','Advanced Check','Execute and validate custom rules','listApplications','applyDerivedColumns','Y','DQ_Sql_Rule,DQ_Rules');

	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;