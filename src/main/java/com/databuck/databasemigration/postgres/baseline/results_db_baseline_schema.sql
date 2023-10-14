
--
-- Table structure for table DATA_MATCHING_PRIMARY_KEY_STATS
--

DROP TABLE IF EXISTS DATA_MATCHING_PRIMARY_KEY_STATS;

CREATE SEQUENCE DATA_MATCHING_PRIMARY_KEY_STATS_seq;

CREATE TABLE DATA_MATCHING_PRIMARY_KEY_STATS (
  id int NOT NULL DEFAULT NEXTVAL ('DATA_MATCHING_PRIMARY_KEY_STATS_seq'),
  idApp int NOT NULL,
  Date date NOT NULL,
  Run int NOT NULL,
  match_value_left_column varchar(2000) DEFAULT NULL,
  match_value_right_column varchar(2000) DEFAULT NULL,
  match_percentage double precision NOT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE DATA_MATCHING_PRIMARY_KEY_STATS_seq RESTART WITH 100;

--
-- Table structure for table DATA_QUALITY_Column_Summary
--

DROP TABLE IF EXISTS DATA_QUALITY_Column_Summary;

CREATE SEQUENCE DATA_QUALITY_Column_Summary_seq;

CREATE TABLE DATA_QUALITY_Column_Summary (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Column_Summary_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  dayOfYear int DEFAULT NULL,
  month varchar(50) DEFAULT NULL,
  dayOfMonth int DEFAULT NULL,
  dayOfWeek varchar(50) DEFAULT NULL,
  hourOfDay int DEFAULT NULL,
  ColName text,
  Count bigint DEFAULT NULL,
  Min double precision DEFAULT NULL,
  Max double precision DEFAULT NULL,
  Cardinality int DEFAULT NULL,
  Std_Dev double precision DEFAULT NULL,
  Mean double precision DEFAULT NULL,
  Null_Value bigint DEFAULT NULL,
  Status varchar(20) DEFAULT NULL,
  StringCardinalityAvg double precision DEFAULT NULL,
  StringCardinalityStdDev double precision DEFAULT NULL,
  StrCardinalityDeviation double precision DEFAULT NULL,
  String_Threshold double precision DEFAULT NULL,
  String_Status varchar(50) DEFAULT NULL,
  NumMeanAvg double precision DEFAULT NULL,
  NumMeanStdDev double precision DEFAULT NULL,
  NumMeanDeviation double precision DEFAULT NULL,
  NumMeanThreshold double precision DEFAULT NULL,
  NumMeanStatus varchar(50) DEFAULT NULL,
  NumSDAvg double precision DEFAULT NULL,
  NumSDStdDev double precision DEFAULT NULL,
  NumSDDeviation double precision DEFAULT NULL,
  NumSDThreshold double precision DEFAULT NULL,
  NumSDStatus varchar(50) DEFAULT NULL,
  outOfNormStatStatus varchar(100) DEFAULT NULL,
  sumOfNumStat double precision DEFAULT NULL,
  NumSumAvg double precision DEFAULT NULL,
  NumSumStdDev double precision DEFAULT NULL,
  NumSumThreshold double precision DEFAULT NULL,
  dGroupVal text,
  dGroupCol text,
  dataDriftCount bigint DEFAULT NULL,
  dataDriftStatus varchar(50) DEFAULT NULL,
  Default_Value bigint DEFAULT NULL,
  Default_Count bigint DEFAULT NULL,
  Record_Count bigint DEFAULT NULL,
  Null_Percentage double precision DEFAULT NULL,
  Default_Percentage double precision DEFAULT NULL,
  Null_Threshold double precision DEFAULT NULL,
  Default_Threshold double precision DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Column_Summary_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_DATA_DRIFT
--

DROP TABLE IF EXISTS DATA_QUALITY_DATA_DRIFT;

CREATE SEQUENCE DATA_QUALITY_DATA_DRIFT_seq;

CREATE TABLE DATA_QUALITY_DATA_DRIFT (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DATA_DRIFT_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  colName text,
  uniqueValues text,
  potentialDuplicates text,
  dGroupVal text,
  dGroupCol text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_DATA_DRIFT_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY
--

DROP TABLE IF EXISTS DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY;

CREATE SEQUENCE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY_seq;

CREATE TABLE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  colName text,
  uniqueValuesCount int DEFAULT NULL,
  missingValueCount int DEFAULT NULL,
  newValueCount int DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  dGroupCol varchar(3000) DEFAULT NULL,
  dGroupVal varchar(3000) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_DATA_DRIFT_SUMMARY
--

DROP TABLE IF EXISTS DATA_QUALITY_DATA_DRIFT_SUMMARY;

CREATE SEQUENCE DATA_QUALITY_DATA_DRIFT_SUMMARY_seq;

CREATE TABLE DATA_QUALITY_DATA_DRIFT_SUMMARY (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DATA_DRIFT_SUMMARY_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  colName text,
  uniqueValues text,
  Operation text,
  dGroupVal text,
  dGroupCol text,
  userName text,
  Time text,
  Status varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_DATA_DRIFT_SUMMARY_seq RESTART WITH 11;

CREATE INDEX data_drift_summary_perf_index ON DATA_QUALITY_DATA_DRIFT_SUMMARY (idApp,Date);

--
-- Table structure for table DATA_QUALITY_DateRule_FailedRecords
--

DROP TABLE IF EXISTS DATA_QUALITY_DateRule_FailedRecords;

CREATE SEQUENCE DATA_QUALITY_DateRule_FailedRecords_seq;

CREATE TABLE DATA_QUALITY_DateRule_FailedRecords (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DateRule_FailedRecords_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  DateFieldCols text,
  DateFieldValues text,
  dGroupVal text,
  dGroupCol text,
  FailureReason text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_DateRule_Reference
--

DROP TABLE IF EXISTS DATA_QUALITY_DateRule_Reference;

CREATE SEQUENCE DATA_QUALITY_DateRule_Reference_seq;

CREATE TABLE DATA_QUALITY_DateRule_Reference (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DateRule_Reference_seq'),
  idApp int DEFAULT NULL,
  DateField text,
  dGroupVal text,
  dGroupCol text,
  MaxAcceptable double precision DEFAULT NULL,
  MinAcceptable double precision DEFAULT NULL,
  FailureReason text,
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_DateRule_Summary
--

DROP TABLE IF EXISTS DATA_QUALITY_DateRule_Summary;

CREATE SEQUENCE DATA_QUALITY_DateRule_Summary_seq;

CREATE TABLE DATA_QUALITY_DateRule_Summary (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_DateRule_Summary_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  DateField text,
  TotalNumberOfRecords bigint DEFAULT NULL,
  TotalFailedRecords bigint DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_Duplicate_Check_Summary
--

DROP TABLE IF EXISTS DATA_QUALITY_Duplicate_Check_Summary;

CREATE SEQUENCE DATA_QUALITY_Duplicate_Check_Summary_seq;

CREATE TABLE DATA_QUALITY_Duplicate_Check_Summary (
  Id int NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Duplicate_Check_Summary_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  Type varchar(50) DEFAULT NULL,
  dGroupCol text,
  dGroupVal text,
  duplicateCheckFields varchar(250) DEFAULT NULL,
  Duplicate bigint DEFAULT NULL,
  TotalCount bigint DEFAULT NULL,
  Percentage double precision DEFAULT NULL,
  Threshold double precision DEFAULT NULL,
  Status varchar(50) DEFAULT NULL,
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_GlobalRules
--

DROP TABLE IF EXISTS DATA_QUALITY_GlobalRules;

CREATE SEQUENCE DATA_QUALITY_GlobalRules_seq;

CREATE TABLE DATA_QUALITY_GlobalRules (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_GlobalRules_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ruleName text,
  dGroupCol text,
  dGroupVal text,
  totalRecords bigint DEFAULT NULL,
  totalFailed bigint DEFAULT NULL,
  rulePercentage double precision DEFAULT NULL,
  ruleThreshold double precision DEFAULT NULL,
  status text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  dimension_name varchar(250) DEFAULT NULL,
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_History_Anomaly
--

DROP TABLE IF EXISTS DATA_QUALITY_History_Anomaly;

CREATE SEQUENCE DATA_QUALITY_History_Anomaly_seq;

CREATE TABLE DATA_QUALITY_History_Anomaly (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_History_Anomaly_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  ColVal int DEFAULT NULL,
  mean double precision DEFAULT NULL,
  stddev double precision DEFAULT NULL,
  dGroupVal text,
  dGroupCol text,
  ra_Deviation double precision DEFAULT NULL,
  RA_Dqi double precision DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_History_Anomaly_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Length_Check
--

DROP TABLE IF EXISTS DATA_QUALITY_Length_Check;

CREATE SEQUENCE DATA_QUALITY_Length_Check_seq;

CREATE TABLE DATA_QUALITY_Length_Check (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Length_Check_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  Length text,
  TotalFailedRecords double precision DEFAULT NULL,
  Length_Threshold double precision DEFAULT NULL,
  RecordCount int DEFAULT NULL,
  FailedRecords_Percentage double precision DEFAULT NULL,
  Status varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  max_length_check_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Length_Check_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_NullCheck_Summary
--

DROP TABLE IF EXISTS DATA_QUALITY_NullCheck_Summary;

CREATE SEQUENCE DATA_QUALITY_NullCheck_Summary_seq;

CREATE TABLE DATA_QUALITY_NullCheck_Summary (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_NullCheck_Summary_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  Record_Count bigint DEFAULT NULL,
  Null_Value bigint DEFAULT NULL,
  Null_Percentage double precision DEFAULT NULL,
  Null_Threshold double precision DEFAULT NULL,
  Status varchar(20) DEFAULT NULL,
  Historic_Null_Mean double precision DEFAULT NULL,
  Historic_Null_stddev double precision DEFAULT NULL,
  Historic_Null_Status varchar(20) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_NullCheck_Summary_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Record_Anomaly
--

DROP TABLE IF EXISTS DATA_QUALITY_Record_Anomaly;

CREATE SEQUENCE DATA_QUALITY_Record_Anomaly_seq;

CREATE TABLE DATA_QUALITY_Record_Anomaly (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Record_Anomaly_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  ColVal double precision DEFAULT NULL,
  mean double precision DEFAULT NULL,
  stddev double precision DEFAULT NULL,
  dGroupVal text,
  dGroupCol text,
  ra_Deviation double precision DEFAULT NULL,
  Status text,
  RA_Dqi double precision DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  threshold double precision DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Record_Anomaly_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Rules
--

DROP TABLE IF EXISTS DATA_QUALITY_Rules;

CREATE SEQUENCE DATA_QUALITY_Rules_seq;

CREATE TABLE DATA_QUALITY_Rules (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Rules_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ruleName text,
  totalRecords bigint DEFAULT NULL,
  totalFailed bigint DEFAULT NULL,
  rulePercentage double precision DEFAULT NULL,
  ruleThreshold double precision DEFAULT NULL,
  status text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_Transaction_Detail_All
--

DROP TABLE IF EXISTS DATA_QUALITY_Transaction_Detail_All;

CREATE SEQUENCE DATA_QUALITY_Transaction_Detail_All_seq;

CREATE TABLE DATA_QUALITY_Transaction_Detail_All (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Transaction_Detail_All_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  duplicateCheckFields text,
  duplicateCheckValues text,
  dupcount bigint DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  dGroupVal varchar(3000) DEFAULT NULL,
  dGroupCol varchar(3000) DEFAULT NULL,
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_Transaction_Detail_Identity
--

DROP TABLE IF EXISTS DATA_QUALITY_Transaction_Detail_Identity;

CREATE SEQUENCE DATA_QUALITY_Transaction_Detail_Identity_seq;

CREATE TABLE DATA_QUALITY_Transaction_Detail_Identity (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Transaction_Detail_Identity_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  duplicateCheckFields text,
  duplicateCheckValues text,
  dupcount bigint DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  dGroupCol text,
  dGroupVal text,
  PRIMARY KEY (Id)
) ;

--
-- Table structure for table DATA_QUALITY_Transaction_Summary
--

DROP TABLE IF EXISTS DATA_QUALITY_Transaction_Summary;

CREATE SEQUENCE DATA_QUALITY_Transaction_Summary_seq;

CREATE TABLE DATA_QUALITY_Transaction_Summary (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Transaction_Summary_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  Duplicate bigint DEFAULT NULL,
  Type varchar(50) DEFAULT NULL,
  TotalCount bigint DEFAULT NULL,
  Percentage double precision DEFAULT NULL,
  Threshold double precision DEFAULT NULL,
  Status varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Transaction_Summary_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Transactionset_sum_A1
--

DROP TABLE IF EXISTS DATA_QUALITY_Transactionset_sum_A1;

CREATE SEQUENCE DATA_QUALITY_Transactionset_sum_A1_seq;

CREATE TABLE DATA_QUALITY_Transactionset_sum_A1 (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Transactionset_sum_A1_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  dayOfYear int DEFAULT NULL,
  month varchar(50) DEFAULT NULL,
  dayOfMonth int DEFAULT NULL,
  dayOfWeek varchar(50) DEFAULT NULL,
  hourOfDay int DEFAULT NULL,
  RecordCount bigint DEFAULT NULL,
  fileNameValidationStatus varchar(20) DEFAULT NULL,
  columnOrderValidationStatus varchar(20) DEFAULT NULL,
  DuplicateDataSet varchar(50) DEFAULT NULL,
  RC_Std_Dev double precision DEFAULT NULL,
  RC_Mean double precision DEFAULT NULL,
  RC_Deviation double precision DEFAULT NULL,
  RC_Std_Dev_Status varchar(50) DEFAULT NULL,
  RC_Mean_Moving_Avg varchar(50) DEFAULT NULL,
  RC_Mean_Moving_Avg_Status varchar(50) DEFAULT NULL,
  measurementCol varchar(2000) DEFAULT NULL,
  SumOf_M_Col double precision DEFAULT NULL,
  M_Std_Dev double precision DEFAULT NULL,
  M_Mean double precision DEFAULT NULL,
  M_Deviation double precision DEFAULT NULL,
  M_Std_Dev_Status varchar(50) DEFAULT NULL,
  M_Mean_Moving_Avg varchar(50) DEFAULT NULL,
  M_Mean_Moving_Avg_Status varchar(50) DEFAULT NULL,
  recordAnomalyCount bigint DEFAULT NULL,
  dGroupVal text,
  dGroupCol text,
  missingDates text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Transactionset_sum_A1_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Transactionset_sum_dgroup
--

DROP TABLE IF EXISTS DATA_QUALITY_Transactionset_sum_dgroup;

CREATE SEQUENCE DATA_QUALITY_Transactionset_sum_dgroup_seq;

CREATE TABLE DATA_QUALITY_Transactionset_sum_dgroup (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Transactionset_sum_dgroup_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  dayOfYear int DEFAULT NULL,
  month varchar(50) DEFAULT NULL,
  dayOfMonth int DEFAULT NULL,
  dayOfWeek varchar(50) DEFAULT NULL,
  hourOfDay int DEFAULT NULL,
  RecordCount bigint DEFAULT NULL,
  RC_Std_Dev double precision DEFAULT NULL,
  RC_Mean double precision DEFAULT NULL,
  dGroupDeviation double precision DEFAULT NULL,
  dGroupRcStatus varchar(50) DEFAULT NULL,
  measurementCol varchar(2000) DEFAULT NULL,
  SumOf_M_Col double precision DEFAULT NULL,
  M_Std_Dev double precision DEFAULT NULL,
  M_Mean double precision DEFAULT NULL,
  M_Deviation double precision DEFAULT NULL,
  M_Std_Dev_Status varchar(50) DEFAULT NULL,
  recordAnomalyCount bigint DEFAULT NULL,
  dGroupVal text,
  dGroupCol text,
  missingDates text,
  fileNameValidationStatus varchar(20) DEFAULT NULL,
  columnOrderValidationStatus varchar(20) DEFAULT NULL,
  DuplicateDataSet varchar(50) DEFAULT NULL,
  Action varchar(100) DEFAULT NULL,
  UserName varchar(100) DEFAULT NULL,
  Time varchar(100) DEFAULT NULL,
  Validity varchar(100) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Transactionset_sum_dgroup_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_Unmatched_Default_Pattern_Data
--

DROP TABLE IF EXISTS DATA_QUALITY_Unmatched_Default_Pattern_Data;

CREATE SEQUENCE DATA_QUALITY_Unmatched_Default_Pattern_Data_seq;

CREATE TABLE DATA_QUALITY_Unmatched_Default_Pattern_Data (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Unmatched_Default_Pattern_Data_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  Col_Name text,
  Total_Records bigint DEFAULT NULL,
  Total_Failed_Records bigint DEFAULT NULL,
  Total_Matched_Records bigint DEFAULT NULL,
  Patterns_List text,
  New_Pattern varchar(5) DEFAULT NULL,
  FailedRecords_Percentage double precision DEFAULT NULL,
  Pattern_Threshold double precision DEFAULT NULL,
  Status varchar(10) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  Csv_File_Path text,
  PRIMARY KEY (Id)
) ;


--
-- Table structure for table DATA_QUALITY_Unmatched_Pattern_Data
--

DROP TABLE IF EXISTS DATA_QUALITY_Unmatched_Pattern_Data;

CREATE SEQUENCE DATA_QUALITY_Unmatched_Pattern_Data_seq;

CREATE TABLE DATA_QUALITY_Unmatched_Pattern_Data (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Unmatched_Pattern_Data_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  Col_Name text,
  Total_Records bigint DEFAULT NULL,
  Total_Failed_Records bigint DEFAULT NULL,
  Pattern_Threshold double precision DEFAULT NULL,
  FailedRecords_Percentage double precision DEFAULT NULL,
  Status text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_Unmatched_Pattern_Data_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_badData
--

DROP TABLE IF EXISTS DATA_QUALITY_badData;

CREATE SEQUENCE DATA_QUALITY_badData_seq;

CREATE TABLE DATA_QUALITY_badData (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_badData_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  TotalRecord int DEFAULT NULL,
  TotalBadRecord int DEFAULT NULL,
  badDataPercentage double precision DEFAULT NULL,
  badDataThreshold double precision DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_badData_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_default_value
--

DROP TABLE IF EXISTS DATA_QUALITY_default_value;

CREATE SEQUENCE DATA_QUALITY_default_value_seq;

CREATE TABLE DATA_QUALITY_default_value (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_default_value_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  ColName text,
  Default_Value text,
  Default_Percentage varchar(50) DEFAULT NULL,
  Default_Count varchar(50) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_default_value_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_frequencyUpdateDate
--

DROP TABLE IF EXISTS DATA_QUALITY_frequencyUpdateDate;

CREATE SEQUENCE DATA_QUALITY_frequencyUpdateDate_seq;

CREATE TABLE DATA_QUALITY_frequencyUpdateDate (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_frequencyUpdateDate_seq'),
  appId int DEFAULT NULL,
  appName text,
  freqUpdateDate date DEFAULT NULL,
  timeSeriesType varchar(100) DEFAULT NULL,
  colName text,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE DATA_QUALITY_frequencyUpdateDate_seq RESTART WITH 11;

--
-- Table structure for table DATA_QUALITY_timeliness_check
--

DROP TABLE IF EXISTS DATA_QUALITY_timeliness_check;

CREATE SEQUENCE DATA_QUALITY_timeliness_check_seq;

CREATE TABLE DATA_QUALITY_timeliness_check (
  Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_timeliness_check_seq'),
  idApp int DEFAULT NULL,
  Date date DEFAULT NULL,
  Run bigint DEFAULT NULL,
  SDate date DEFAULT NULL,
  EDate date DEFAULT NULL,
  TimelinessKey varchar(500) DEFAULT NULL,
  No_Of_Days varchar(10) DEFAULT NULL,
  Status varchar(50) DEFAULT NULL,
  TotalCount int DEFAULT NULL,
  TotalFailedCount int DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (Id)
)  ;

--
-- Table structure for table DQ_JIRA_MSG_QUEUE
--

DROP TABLE IF EXISTS DQ_JIRA_MSG_QUEUE;

CREATE SEQUENCE DQ_JIRA_MSG_QUEUE_seq;

CREATE TABLE DQ_JIRA_MSG_QUEUE (
  id int NOT NULL DEFAULT NEXTVAL ('DQ_JIRA_MSG_QUEUE_seq'),
  msg_body text NOT NULL,
  ticket_process_status varchar(20) NOT NULL DEFAULT 'N',
  ticket_submit_status varchar(20) NOT NULL DEFAULT 'N',
  createdAt timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table DQ_SNS_MSG_QUEUE
--

DROP TABLE IF EXISTS DQ_SNS_MSG_QUEUE;

CREATE SEQUENCE DQ_SNS_MSG_QUEUE_seq;

CREATE TABLE DQ_SNS_MSG_QUEUE (
  id bigint NOT NULL DEFAULT NEXTVAL ('DQ_SNS_MSG_QUEUE_seq'),
  msg_id text,
  sns_msg_body text,
  sns_alert_enabled varchar(10) DEFAULT 'N',
  sns_alert_sent varchar(10) DEFAULT 'N',
  createdAt timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE DQ_SNS_MSG_QUEUE_seq RESTART WITH 11;

--
-- Table structure for table DQ_SQS_MSG_QUEUE
--

DROP TABLE IF EXISTS DQ_SQS_MSG_QUEUE;

CREATE SEQUENCE DQ_SQS_MSG_QUEUE_seq;

CREATE TABLE DQ_SQS_MSG_QUEUE (
  id bigint NOT NULL DEFAULT NEXTVAL ('DQ_SQS_MSG_QUEUE_seq'),
  uniqueId varchar(1000) NOT NULL,
  idApp bigint NOT NULL,
  execution_date date DEFAULT NULL,
  run bigint DEFAULT NULL,
  sqs_alert_enabled varchar(10) DEFAULT 'N',
  sqs_alert_sent varchar(10) DEFAULT 'N',
  createdAt timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT con_dq_sqs_uniqueId UNIQUE (uniqueId)
)  ;

ALTER SEQUENCE DQ_SQS_MSG_QUEUE_seq RESTART WITH 11;

--
-- Table structure for table DashBoard_Summary
--

DROP TABLE IF EXISTS DashBoard_Summary;

CREATE TABLE DashBoard_Summary (
  Date varchar(100) DEFAULT NULL,
  Run int DEFAULT NULL,
  AppId int DEFAULT NULL,
  DQI double precision DEFAULT NULL,
  Status varchar(50) DEFAULT NULL,
  Key_Metric_1 double precision DEFAULT NULL,
  Key_Metric_2 double precision DEFAULT NULL,
  Key_Metric_3 text,
  Test varchar(100) DEFAULT NULL
) ;


--
-- Table structure for table app_option_list
--

DROP TABLE IF EXISTS app_option_list;

CREATE SEQUENCE app_option_list_seq;

CREATE TABLE app_option_list (
  row_id int NOT NULL DEFAULT NEXTVAL ('app_option_list_seq'),
  list_reference varchar(255) NOT NULL,
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT list_reference UNIQUE (list_reference)
)  ;

ALTER SEQUENCE app_option_list_seq RESTART WITH 4;

--
-- Table structure for table app_option_list_elements
--

DROP TABLE IF EXISTS app_option_list_elements;

CREATE SEQUENCE app_option_list_elements_seq;

CREATE TABLE app_option_list_elements (
  row_id int NOT NULL DEFAULT NEXTVAL ('app_option_list_elements_seq'),
  elements2app_list int NOT NULL,
  element_reference varchar(255) NOT NULL,
  element_text varchar(500) NOT NULL,
  is_default smallint NOT NULL DEFAULT 0,
  position int NOT NULL,
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT element_reference UNIQUE (elements2app_list,element_reference)
)  ;

ALTER SEQUENCE app_option_list_elements_seq RESTART WITH 9;

--
-- Dumping data for table app_option_list_elements
--

INSERT INTO app_option_list_elements VALUES (1,1,'NOT_STARTED','Not Started', 0,1,1),(2,1,'REVIEWED','Reviewed', 0,2,1),(3,1,'APPROVED','Approved', 0,3,1),(4,2,'RESEARCH','Research',1,1,1),(5,2,'CAN_PROCEED','Can Proceed', 0,2,1),(6,2,'NEEDS_TO_STOP','Needs to Stop', 0,3,1),(7,3,'APPROVED','Approved', 0,1,1),(8,3,'NOT_APPROVED','Not Approved',1,2,1);

--
-- Table structure for table app_uniqueId_master_table
--

DROP TABLE IF EXISTS app_uniqueId_master_table;

CREATE SEQUENCE app_uniqueId_master_table_seq;

CREATE TABLE app_uniqueId_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('app_uniqueId_master_table_seq'),
  uniqueId varchar(1000) NOT NULL,
  idapp bigint NOT NULL,
  execution_date date DEFAULT NULL,
  run bigint DEFAULT NULL,
  test_run varchar(2500) DEFAULT 'N',
  PRIMARY KEY (id),
  CONSTRAINT con_app_uniqueId UNIQUE (uniqueId)
)  ;

ALTER SEQUENCE app_uniqueId_master_table_seq RESTART WITH 11;

--
-- Table structure for table column_combination_profile_master_table
--

DROP TABLE IF EXISTS column_combination_profile_master_table;

CREATE SEQUENCE column_combination_profile_master_table_seq;

CREATE TABLE column_combination_profile_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('column_combination_profile_master_table_seq'),
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  idData bigint DEFAULT NULL,
  idDataSchema bigint DEFAULT NULL,
  folderPath text,
  table_or_fileName text,
  Column_Group_Name varchar(1000) DEFAULT NULL,
  Column_Group_Value varchar(1000) DEFAULT NULL,
  Count bigint DEFAULT NULL,
  Percentage decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE column_combination_profile_master_table_seq RESTART WITH 8;

--
-- Table structure for table column_profile_detail_master_table
--

DROP TABLE IF EXISTS column_profile_detail_master_table;

CREATE SEQUENCE column_profile_detail_master_table_seq;

CREATE TABLE column_profile_detail_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('column_profile_detail_master_table_seq'),
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  idData bigint DEFAULT NULL,
  idDataSchema bigint DEFAULT NULL,
  folderPath text,
  table_or_fileName text,
  Column_Name varchar(1000) DEFAULT NULL,
  Column_Value varchar(1000) DEFAULT NULL,
  Count bigint DEFAULT NULL,
  Percentage decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE column_profile_detail_master_table_seq RESTART WITH 8;

--
-- Table structure for table column_profile_master_table
--

DROP TABLE IF EXISTS column_profile_master_table;

CREATE SEQUENCE column_profile_master_table_seq;

CREATE TABLE column_profile_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('column_profile_master_table_seq'),
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  idDataSchema bigint DEFAULT NULL,
  folderPath text,
  table_or_fileName text,
  Column_Name varchar(1000) DEFAULT NULL,
  Data_Type varchar(1000) DEFAULT NULL,
  Total_Record_Count bigint DEFAULT NULL,
  Missing_Value bigint DEFAULT NULL,
  Percentage_Missing decimal(5,2) DEFAULT NULL,
  Unique_Count bigint DEFAULT NULL,
  Min_Length int DEFAULT NULL,
  Max_Length int DEFAULT NULL,
  Mean varchar(1000) DEFAULT NULL,
  Std_Dev varchar(1000) DEFAULT NULL,
  Min varchar(1000) DEFAULT NULL,
  Max varchar(1000) DEFAULT NULL,
  "99_percentaile" varchar(1000) DEFAULT NULL,
  "75_percentile" varchar(1000) DEFAULT NULL,
  "25_percentile" varchar(1000) DEFAULT NULL,
  "1_percentile" varchar(1000) DEFAULT NULL,
  idData bigint DEFAULT NULL,
  Default_Patterns text,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE column_profile_master_table_seq RESTART WITH 8;

--
-- Table structure for table data_matching_dashboard
--

DROP TABLE IF EXISTS data_matching_dashboard;

CREATE SEQUENCE data_matching_dashboard_seq;

CREATE TABLE data_matching_dashboard (
  id bigint NOT NULL DEFAULT NEXTVAL ('data_matching_dashboard_seq'),
  idapp bigint DEFAULT NULL,
  date date DEFAULT NULL,
  run bigint DEFAULT NULL,
  validationCheckName text,
  source1Name text,
  source2Name text,
  source1Count bigint DEFAULT NULL,
  source1OnlyRecords bigint DEFAULT NULL,
  source1Status text,
  source2Count bigint DEFAULT NULL,
  source2OnlyRecords bigint DEFAULT NULL,
  source2Status text,
  unMatchedRecords bigint DEFAULT NULL,
  unMatchedStatus text,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE data_matching_dashboard_seq RESTART WITH 11;

--
-- Table structure for table data_quality_approval_log
--

DROP TABLE IF EXISTS data_quality_approval_log;

CREATE SEQUENCE data_quality_approval_log_seq;

CREATE TABLE data_quality_approval_log (
  row_id int NOT NULL DEFAULT NEXTVAL ('data_quality_approval_log_seq'),
  idApp bigint NOT NULL,
  date timestamp(0) NOT NULL,
  run int NOT NULL,
  action_type varchar(1) NOT NULL,
  action_state int DEFAULT NULL,
  action_comments varchar(2000) DEFAULT NULL,
  action_date timestamp(0) DEFAULT NULL,
  action_by bigint DEFAULT NULL,
  entry_date date NOT NULL,
  PRIMARY KEY (row_id)
) ;

--
-- Table structure for table data_quality_dashboard
--

DROP TABLE IF EXISTS data_quality_dashboard;

CREATE SEQUENCE data_quality_dashboard_seq;

CREATE TABLE data_quality_dashboard (
  id bigint NOT NULL DEFAULT NEXTVAL ('data_quality_dashboard_seq'),
  IdApp bigint NOT NULL,
  date date DEFAULT NULL,
  run bigint DEFAULT NULL,
  validationCheckName text,
  sourceName text,
  recordCountStatus text,
  nullCountStatus text,
  primaryKeyStatus text,
  userSelectedFieldStatus text,
  numericalFieldStatus text,
  stringFieldStatus text,
  recordAnomalyStatus text,
  dataDriftStatus text,
  aggregateDQI double precision DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE data_quality_dashboard_seq RESTART WITH 89;

--
-- Table structure for table data_quality_historic_dashboard
--

DROP TABLE IF EXISTS data_quality_historic_dashboard;

CREATE SEQUENCE data_quality_historic_dashboard_seq;

CREATE TABLE data_quality_historic_dashboard (
  id bigint NOT NULL DEFAULT NEXTVAL ('data_quality_historic_dashboard_seq'),
  idApp bigint NOT NULL,
  validationcheckname text,
  date date NOT NULL,
  run bigint NOT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  dataSetName varchar(200) DEFAULT NULL,
  testType varchar(200) DEFAULT NULL,
  aggregateDQI double precision DEFAULT '0',
  fileContentValidationStatus varchar(20) DEFAULT NULL,
  columnOrderValidationStatus varchar(20) DEFAULT NULL,
  absoluteRCDQI double precision DEFAULT '0',
  absoluteRCStatus varchar(20) DEFAULT NULL,
  absoluteRCRecordCount bigint DEFAULT NULL,
  absoluteRCAverageRecordCount bigint DEFAULT NULL,
  aggregateRCDQI double precision DEFAULT '0',
  aggregateRCStatus varchar(20) DEFAULT NULL,
  aggregateRCRecordCount bigint DEFAULT NULL,
  nullCountDQI double precision DEFAULT '0',
  nullCountStatus varchar(20) DEFAULT NULL,
  nullCountColumns bigint DEFAULT NULL,
  nullCountColumnsFailed bigint DEFAULT NULL,
  primaryKeyDQI double precision DEFAULT '0',
  primaryKeyStatus varchar(20) DEFAULT NULL,
  primaryKeyDuplicates bigint DEFAULT NULL,
  userSelectedDQI double precision DEFAULT '0',
  userSelectedStatus varchar(20) DEFAULT NULL,
  userSelectedDuplicates bigint DEFAULT NULL,
  numericalDQI double precision DEFAULT '0',
  numericalStatus varchar(20) DEFAULT NULL,
  numericalColumns bigint DEFAULT NULL,
  numericalRecordsFailed bigint DEFAULT NULL,
  stringDQI double precision DEFAULT '0',
  stringStatus varchar(20) DEFAULT NULL,
  stringColumns bigint DEFAULT NULL,
  stringRecordsFailed bigint DEFAULT NULL,
  recordAnomalyDQI double precision DEFAULT '0',
  recordAnomalyStatus varchar(20) DEFAULT NULL,
  recordAnomalyRecords bigint DEFAULT NULL,
  recordAnomalyRecordsFailed bigint DEFAULT NULL,
  ruleType varchar(20) DEFAULT NULL,
  ruleDQI double precision DEFAULT '0',
  dataDriftDQI double precision DEFAULT '0',
  dataDriftStatus varchar(20) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table data_quality_sql_rules
--

DROP TABLE IF EXISTS data_quality_sql_rules;

CREATE SEQUENCE data_quality_sql_rules_seq;

CREATE TABLE data_quality_sql_rules (
  id int NOT NULL DEFAULT NEXTVAL ('data_quality_sql_rules_seq'),
  date date NOT NULL,
  idapp int NOT NULL,
  run int NOT NULL,
  ruleName text,
  total_failed_records int NOT NULL,
  status smallint NOT NULL,
  top_failed_data text,
  forgot_run_enabled varchar(10) DEFAULT 'N',
  ruleThreshold double precision DEFAULT '0',
  totalRecords bigint DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE data_quality_sql_rules_seq RESTART WITH 14;

--
-- Table structure for table dbk_fm_audit_details
--

DROP TABLE IF EXISTS dbk_fm_audit_details;

CREATE SEQUENCE dbk_fm_audit_details_seq;

CREATE TABLE dbk_fm_audit_details (
  fm_activity_Id int NOT NULL DEFAULT NEXTVAL ('dbk_fm_audit_details_seq'),
  fm_activity_name varchar(500) DEFAULT NULL,
  execution_date date DEFAULT NULL,
  execution_start_time timestamp(3) DEFAULT NULL,
  execution_end_time timestamp(3) DEFAULT NULL,
  status varchar(1000) NOT NULL,
  PRIMARY KEY (fm_activity_Id)
)  ;

ALTER SEQUENCE dbk_fm_audit_details_seq RESTART WITH 100;

--
-- Table structure for table dbk_fm_filearrival_details
--

DROP TABLE IF EXISTS dbk_fm_filearrival_details;

CREATE SEQUENCE dbk_fm_filearrival_details_seq;

CREATE TABLE dbk_fm_filearrival_details (
  Id int NOT NULL DEFAULT NEXTVAL ('dbk_fm_filearrival_details_seq'),
  connection_id int NOT NULL,
  validation_id int NOT NULL,
  schema_name varchar(250) NOT NULL,
  table_or_subfolder_name varchar(1000) DEFAULT NULL,
  file_indicator varchar(50) DEFAULT NULL,
  dayOfWeek varchar(50) NOT NULL,
  load_date date DEFAULT NULL,
  loaded_hour smallint DEFAULT NULL,
  loaded_time smallint DEFAULT NULL,
  size_or_record_count int NOT NULL,
  size_or_record_count_check varchar(50) DEFAULT NULL,
  column_metadata_check varchar(50) DEFAULT NULL,
  file_validity_status varchar(50) DEFAULT NULL,
  file_arrival_status varchar(50) DEFAULT NULL,
  expected_hour smallint DEFAULT NULL,
  expected_time smallint DEFAULT NULL,
  file_name varchar(1000) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE dbk_fm_filearrival_details_seq RESTART WITH 100;

--
-- Table structure for table dbk_fm_load_history_table
--

DROP TABLE IF EXISTS dbk_fm_load_history_table;

CREATE SEQUENCE dbk_fm_load_history_table_seq;

CREATE TABLE dbk_fm_load_history_table (
  Id int NOT NULL DEFAULT NEXTVAL ('dbk_fm_load_history_table_seq'),
  connection_id int NOT NULL,
  validation_id int NOT NULL,
  connection_type varchar(100) DEFAULT NULL,
  schema_name varchar(250) NOT NULL,
  table_or_subfolder_name varchar(1000) DEFAULT NULL,
  record_count int DEFAULT NULL,
  last_load_time timestamp(3) DEFAULT NULL,
  last_altered timestamp(3) DEFAULT NULL,
  file_name varchar(1000) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE dbk_fm_load_history_table_seq RESTART WITH 100;

--
-- Table structure for table dbk_fm_summary_details
--

DROP TABLE IF EXISTS dbk_fm_summary_details;

CREATE SEQUENCE dbk_fm_summary_details_seq;

CREATE TABLE dbk_fm_summary_details (
  Id int NOT NULL DEFAULT NEXTVAL ('dbk_fm_summary_details_seq'),
  connection_id int NOT NULL,
  validation_id int NOT NULL,
  schema_name varchar(250) NOT NULL,
  table_or_subfolder_name varchar(1000) DEFAULT NULL,
  file_indicator varchar(50) DEFAULT NULL,
  dayOfWeek varchar(50) NOT NULL,
  load_date date DEFAULT NULL,
  loaded_hour smallint DEFAULT NULL,
  expected_minute smallint DEFAULT NULL,
  actual_file_count smallint DEFAULT NULL,
  expected_file_count smallint DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE dbk_fm_summary_details_seq RESTART WITH 100;

--
-- Table structure for table external_api_alert_msg_queue
--

DROP TABLE IF EXISTS external_api_alert_msg_queue;

CREATE SEQUENCE external_api_alert_msg_queue_seq;

CREATE TABLE external_api_alert_msg_queue (
  id int NOT NULL DEFAULT NEXTVAL ('external_api_alert_msg_queue_seq'),
  external_api_type varchar(100) NOT NULL,
  taskType varchar(2500) NOT NULL,
  taskId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  execution_date date DEFAULT NULL,
  run int DEFAULT NULL,
  test_run varchar(20) DEFAULT 'N',
  alter_timeStamp timestamp(0) DEFAULT NULL,
  alert_msg varchar(2500) DEFAULT NULL,
  alert_msg_code varchar(2500) DEFAULT NULL,
  alert_label varchar(2500) DEFAULT NULL,
  alert_json text,
  alert_msg_deliver_status varchar(2500) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table file_management_run
--

DROP TABLE IF EXISTS file_management_run;

CREATE SEQUENCE file_management_run_seq;

CREATE TABLE file_management_run (
  id int NOT NULL DEFAULT NEXTVAL ('file_management_run_seq'),
  appId int NOT NULL,
  dateOfRun timestamp(0) NOT NULL,
  fileName varchar(500) NOT NULL,
  hashCode bigint DEFAULT NULL,
  missingFiles text,
  extraFiles text,
  realFileName varchar(100) DEFAULT NULL,
  lastProcessedTimestamp varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table historical_matching_run
--

DROP TABLE IF EXISTS historical_matching_run;

CREATE SEQUENCE historical_matching_run_seq;

CREATE TABLE historical_matching_run (
  id int NOT NULL DEFAULT NEXTVAL ('historical_matching_run_seq'),
  appId int NOT NULL,
  Run int NOT NULL,
  Date varchar(100) NOT NULL,
  tableName varchar(100) NOT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table numerical_profile_master_table
--

DROP TABLE IF EXISTS numerical_profile_master_table;

CREATE SEQUENCE numerical_profile_master_table_seq;

CREATE TABLE numerical_profile_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('numerical_profile_master_table_seq'),
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  idData bigint DEFAULT NULL,
  idDataSchema bigint DEFAULT NULL,
  folderPath text,
  table_or_fileName text,
  Column_Name_1 varchar(1000) DEFAULT NULL,
  Column_Name_2 varchar(1000) DEFAULT NULL,
  Correlation decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE numerical_profile_master_table_seq RESTART WITH 8;

--
-- Table structure for table processData
--

DROP TABLE IF EXISTS processData;

CREATE TABLE processData (
  idApp bigint DEFAULT NULL,
  Run int DEFAULT NULL,
  Date date DEFAULT NULL,
  folderName varchar(500) DEFAULT NULL,
  forgot_run_enabled varchar(10) DEFAULT 'N'
) ;

--
-- Table structure for table result_master_table
--

DROP TABLE IF EXISTS result_master_table;

CREATE TABLE result_master_table (
  appID int DEFAULT NULL,
  AppName text,
  AppType text,
  Result_Category text,
  Result_Category1 text,
  Result_Category2 text,
  Result_Type text,
  Table_Name text,
  project_id bigint DEFAULT NULL
) ;

--
-- Table structure for table roll_data_matching_dashboard
--

DROP TABLE IF EXISTS roll_data_matching_dashboard;

CREATE SEQUENCE roll_data_matching_dashboard_seq;

CREATE TABLE roll_data_matching_dashboard (
  id bigint NOT NULL DEFAULT NEXTVAL ('roll_data_matching_dashboard_seq'),
  idapp bigint DEFAULT NULL,
  date date DEFAULT NULL,
  run bigint DEFAULT NULL,
  validationCheckName text,
  source1Name text,
  source2Name text,
  source1Count bigint DEFAULT NULL,
  source1OnlyRecords bigint DEFAULT NULL,
  source1Status text,
  source2Count bigint DEFAULT NULL,
  source2OnlyRecords bigint DEFAULT NULL,
  source2Status text,
  unMatchedRecords bigint DEFAULT NULL,
  unMatchedStatus text,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE roll_data_matching_dashboard_seq RESTART WITH 11;

--
-- Table structure for table row_profile_master_table
--

DROP TABLE IF EXISTS row_profile_master_table;

CREATE SEQUENCE row_profile_master_table_seq;

CREATE TABLE row_profile_master_table (
  id bigint NOT NULL DEFAULT NEXTVAL ('row_profile_master_table_seq'),
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  idData bigint DEFAULT NULL,
  idDataSchema bigint DEFAULT NULL,
  folderPath text,
  table_or_fileName text,
  Number_of_Columns_with_NULL bigint DEFAULT NULL,
  Number_of_Records bigint DEFAULT NULL,
  Percentage_Missing decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE row_profile_master_table_seq RESTART WITH 8;

--
-- Table structure for table schema_version
--

DROP TABLE IF EXISTS schema_version;

CREATE TABLE schema_version (
  installed_rank int NOT NULL,
  version varchar(50) DEFAULT NULL,
  description varchar(200) NOT NULL,
  type varchar(20) NOT NULL,
  script varchar(1000) NOT NULL,
  checksum int DEFAULT NULL,
  installed_by varchar(100) NOT NULL,
  installed_on timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  execution_time int NOT NULL,
  success boolean NOT NULL,
  PRIMARY KEY (installed_rank)
) ;

CREATE INDEX schema_version_s_idx ON schema_version (success);

--
-- Table structure for table sub_task_status
--

DROP TABLE IF EXISTS sub_task_status;

CREATE SEQUENCE sub_task_status_seq;

CREATE TABLE sub_task_status (
  id bigint NOT NULL DEFAULT NEXTVAL ('sub_task_status_seq'),
  Date date DEFAULT NULL,
  idapp bigint DEFAULT NULL,
  appname varchar(1000) DEFAULT NULL,
  rca varchar(50) DEFAULT NULL,
  gbrca varchar(50) DEFAULT NULL,
  numstat varchar(50) DEFAULT NULL,
  strstat varchar(50) DEFAULT NULL,
  nullcheck varchar(50) DEFAULT NULL,
  dupidcheck varchar(50) DEFAULT NULL,
  dupallcheck varchar(50) DEFAULT NULL,
  ra varchar(50) DEFAULT NULL,
  datadrift varchar(50) DEFAULT NULL,
  rules varchar(10) DEFAULT NULL,
  globalrules varchar(10) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE sub_task_status_seq RESTART WITH 134;

--
-- Table structure for table task_progress_status
--

DROP TABLE IF EXISTS task_progress_status;

CREATE SEQUENCE task_progress_status_seq;

CREATE TABLE task_progress_status (
  id bigint NOT NULL DEFAULT NEXTVAL ('task_progress_status_seq'),
  Date date DEFAULT NULL,
  idapp bigint DEFAULT NULL,
  appname varchar(1000) DEFAULT NULL,
  rca varchar(50) DEFAULT NULL,
  gbrca varchar(50) DEFAULT NULL,
  numstat varchar(50) DEFAULT NULL,
  strstat varchar(50) DEFAULT NULL,
  nullcheck varchar(50) DEFAULT NULL,
  dupidcheck varchar(50) DEFAULT NULL,
  dupallcheck varchar(50) DEFAULT NULL,
  ra varchar(50) DEFAULT NULL,
  datadrift varchar(50) DEFAULT NULL,
  rules varchar(10) DEFAULT NULL,
  dfread varchar(1000) DEFAULT NULL,
  dfread2 varchar(1000) DEFAULT NULL,
  matchingStatus varchar(1000) DEFAULT NULL,
  schemaMatchingTotal bigint DEFAULT NULL,
  schemaMatchingCompleted bigint DEFAULT NULL,
  defaultcheck varchar(45) DEFAULT NULL,
  TimelinessKeyCheck varchar(45) DEFAULT NULL,
  patternCheck varchar(45) DEFAULT NULL,
  lengthCheck varchar(50) DEFAULT NULL,
  badData varchar(50) DEFAULT NULL,
  globalrules varchar(10) DEFAULT NULL,
  maxLengthCheck varchar(50) DEFAULT NULL,
  defaultPatternCheck varchar(10) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE task_progress_status_seq RESTART WITH 134;

--
-- Table structure for table template_task_status
--

DROP TABLE IF EXISTS template_task_status;

CREATE SEQUENCE template_task_status_seq;

CREATE TABLE template_task_status (
  id int NOT NULL DEFAULT NEXTVAL ('template_task_status_seq'),
  idData int NOT NULL,
  taskName varchar(500) NOT NULL,
  status varchar(50) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE template_task_status_seq RESTART WITH 1759;

