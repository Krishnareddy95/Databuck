

--
-- Table structure for table Module
--

DROP TABLE IF EXISTS Module;

CREATE SEQUENCE Module_seq;

CREATE TABLE Module (
  idTask int NOT NULL DEFAULT NEXTVAL ('Module_seq'),
  taskName varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  PRIMARY KEY (idTask)
)  ;

ALTER SEQUENCE Module_seq RESTART WITH 14;

--
-- Dumping data for table Module
--

INSERT INTO Module VALUES (1,'Data Connection','2017-06-16 10:53:49','2017-06-16 10:53:49'),(2,'Data Template','2017-06-16 10:53:49','2017-06-16 10:53:49'),(3,'Extend Template & Rule','2017-06-16 10:53:49','2017-06-16 10:53:49'),(4,'Validation Check','2017-06-16 10:53:49','2017-06-16 10:53:49'),(5,'Tasks','2017-06-16 10:53:49','2017-06-16 10:53:49'),(6,'Results','2017-06-16 10:53:49','2017-06-16 10:53:49'),(7,'User Settings','2017-06-16 10:53:49','2017-06-16 10:53:49'),(8,'Global Rule','2022-07-19 17:57:14','2022-07-19 17:57:14'),(9,'Dash Configuration','2022-07-19 17:57:14','2022-07-19 17:57:14'),(10,'Dashboard','2022-07-19 17:57:15','2022-07-19 17:57:15'),(11,'Application Settings','2022-07-19 17:57:15','2022-07-19 17:57:15'),(12,'QuickStart','2022-07-19 17:57:16','2022-07-19 17:57:16'),(13,'MyViews','2022-07-19 17:57:20','2022-07-19 17:57:20');

--
-- Table structure for table Role
--

DROP TABLE IF EXISTS Role;

CREATE SEQUENCE Role_seq;

CREATE TABLE Role (
  idRole int NOT NULL DEFAULT NEXTVAL ('Role_seq'),
  roleName varchar(100) DEFAULT NULL,
  description varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  PRIMARY KEY (idRole),
  CONSTRAINT roleName UNIQUE (roleName)
)  ;

ALTER SEQUENCE Role_seq RESTART WITH 8;

--
-- Dumping data for table Role
--

INSERT INTO Role VALUES (1,'Admin','Admin Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(2,'Marketer','Marketer Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(3,'Assistant','','2016-03-15 08:09:51','2016-03-15 08:09:51'),(4,'Developer','Group for Developers','2016-03-15 05:56:52','2016-03-15 05:56:52'),(5,'Students','','2016-03-15 08:00:31','2016-03-15 08:00:31'),(6,'group1','','2016-03-17 07:49:37','2016-03-17 07:49:37'),(7,'Tester','','2019-04-11 05:15:21','2019-04-11 05:15:21');

--
-- Table structure for table RoleModule
--

DROP TABLE IF EXISTS RoleModule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE RoleModule_seq;

CREATE TABLE RoleModule (
  idRoleTask int NOT NULL DEFAULT NEXTVAL ('RoleModule_seq'),
  idRole int DEFAULT NULL,
  idTask int DEFAULT NULL,
  accessControl varchar(50) DEFAULT NULL,
  PRIMARY KEY (idRoleTask)
)  ;

ALTER SEQUENCE RoleModule_seq RESTART WITH 90;

CREATE INDEX idRole ON RoleModule (idRole);
CREATE INDEX idTask ON RoleModule (idTask);

--
-- Dumping data for table RoleModule
--

INSERT INTO RoleModule VALUES (1,1,1,'C-R-U-D'),(2,1,2,'C-R-U-D'),(3,1,3,'C-R-U-D'),(4,1,4,'C-R-U-D'),(5,1,5,'C-R-U-D'),(6,1,6,'C-R-U-D'),(7,1,7,'C-R-U-D'),(11,4,6,'C-R'),(51,3,1,'C-R-U-D'),(52,3,2,'C-R-U-D'),(53,3,3,'C-R-U-D'),(54,3,4,'C-R-U-D'),(55,3,5,'C-R-U-D'),(56,3,6,'C-R-U-D'),(75,2,1,'R-U-D'),(76,2,2,'C-R-U-D'),(77,2,3,'C-R'),(78,2,4,'C-R-U-D'),(79,2,5,'C-R-U-D'),(80,2,6,'R-D'),(83,7,1,'C-R-U-D'),(84,7,2,'C-R-U'),(85,1,8,'C-R-U-D'),(86,1,9,'C-R-U-D'),(87,1,10,'C-R-U-D'),(88,1,11,'C-R-U-D'),(89,1,12,'C-R-U-D');

--
-- Table structure for table SynonymLibrary
--

DROP TABLE IF EXISTS SynonymLibrary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE SynonymLibrary_seq;

CREATE TABLE SynonymLibrary (
  synonyms_Id int NOT NULL DEFAULT NEXTVAL ('SynonymLibrary_seq'),
  domain_Id int NOT NULL,
  tableColumn varchar(200) DEFAULT NULL,
  possiblenames varchar(200) DEFAULT NULL,
  PRIMARY KEY (synonyms_Id),
  CONSTRAINT SynonymsName UNIQUE (domain_Id,tableColumn)
) ;

--
-- Table structure for table User
--

DROP TABLE IF EXISTS "User";
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE User_seq;

CREATE TABLE "User" (
  idUser int NOT NULL DEFAULT NEXTVAL ('User_seq'),
  firstName varchar(100) DEFAULT NULL,
  lastName varchar(100) DEFAULT NULL,
  salt varchar(100) DEFAULT NULL,
  password varchar(150) DEFAULT NULL,
  company varchar(100) DEFAULT NULL,
  department varchar(100) DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  active smallint DEFAULT '1',
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  userType smallint DEFAULT '0',
  PRIMARY KEY (idUser)
)  ;

ALTER SEQUENCE User_seq RESTART WITH 5;

--
-- Dumping data for table User
--

INSERT INTO "User" VALUES (1,'Admin','User','09b2271b67c6d537b1dd55a294f43ab3','$2a$10$p4It/9O4qq03P5XADE9eyObiGA9esqjPxERjDhGcamNWDxoTI9hsK','DataBuck','DataBuck','admin@databuck.com',1,'2016-03-12 00:00:00','2016-03-12 00:00:00',1),(2,'test','user',NULL,'$2a$10$pzaPyKG0yLmzrpAEnUOwJObckYAPDKQYfEcK9HlghTd5cffZhhEwa',NULL,NULL,'testuser@databuck.com',1,'2020-01-09 06:57:12','2020-01-09 06:57:12',1),(3,'User','Test',NULL,'$2a$10$9njMn3e266ZuP9z/4WDc6uaaFzJEzk1.2GLph5/V6e6ddrNHonrey',NULL,NULL,'usertest@databuck.com',1,'2020-01-09 08:30:22','2020-01-09 08:30:22',1),(4,'PPP','KKK',NULL,'$2a$10$2tRsA/65zhT8ZM6qcE1eq..2FrNyhk46tN/xONNTw7.RlezyAEjgq',NULL,NULL,'pk@databuck.com',1,'2020-01-09 08:32:44','2020-01-09 08:32:44',1);

--
-- Table structure for table UserRole
--

DROP TABLE IF EXISTS UserRole;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE UserRole_seq;

CREATE TABLE UserRole (
  idUserRole int NOT NULL DEFAULT NEXTVAL ('UserRole_seq'),
  idUser int DEFAULT NULL,
  idRole int DEFAULT NULL,
  PRIMARY KEY (idUserRole)
)  ;

ALTER SEQUENCE UserRole_seq RESTART WITH 21;

CREATE INDEX index_idUser ON UserRole (idUser);
CREATE INDEX index_idRole ON UserRole (idRole);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table UserRole
--

INSERT INTO UserRole VALUES (1,1,1),(2,2,2),(20,4,1);

--
-- Table structure for table appConfig
--

DROP TABLE IF EXISTS appConfig;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE appConfig_seq;

CREATE TABLE appConfig (
  id int NOT NULL DEFAULT NEXTVAL ('appConfig_seq'),
  numberExecutor int NOT NULL,
  numberApp int NOT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table appGroupMapping
--

DROP TABLE IF EXISTS appGroupMapping;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE appGroupMapping_seq;

CREATE TABLE appGroupMapping (
  idAppGroupMapping int NOT NULL DEFAULT NEXTVAL ('appGroupMapping_seq'),
  idAppGroup int NOT NULL,
  idApp int NOT NULL,
  PRIMARY KEY (idAppGroupMapping)
) ;


--
-- Table structure for table app_option_list
--

DROP TABLE IF EXISTS app_option_list;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE app_option_list_seq;

CREATE TABLE app_option_list (
  row_id int NOT NULL DEFAULT NEXTVAL ('app_option_list_seq'),
  list_reference varchar(255) NOT NULL,
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT list_reference UNIQUE (list_reference)
)  ;

ALTER SEQUENCE app_option_list_seq RESTART WITH 3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table app_option_list
--

INSERT INTO app_option_list VALUES (1,'GLOBAL_THRESHOLDS_OPTION','1'),(2,'DQ_RULE_CATALOG_STATUS','1');

--
-- Table structure for table app_option_list_elements
--

DROP TABLE IF EXISTS app_option_list_elements;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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

ALTER SEQUENCE app_option_list_elements_seq RESTART WITH 23;

--
-- Dumping data for table app_option_list_elements
--

INSERT INTO app_option_list_elements VALUES (1,1,'USE_BASE_VALUE','Use base threshold value from data template',0,1,1),(2,1,'USE_GLOBAL_THRESHOLD_VALUE','Use value entered in global threshold page',0,2,1),(3,1,'USE_USER_REJECTED_VALUE','Use value rejected from dashboard page',0,3,1),(4,1,'USE_MOVING_AVERAGE_VALUE','Use value as auto adjusting moving average',0,4,1),(5,2,'NOT APPROVED','Not Approved',0,1,0),(6,2,'READY FOR TEST','Ready For Test',0,2, 0),(7,2,'APPROVED FOR TEST','Approved For Test', 0,3,0),(8,2,'READY FOR EXPORT','Ready For Export',0,4,0),(9,2,'APPROVED FOR EXPORT','Approved For Export',0,5, 0),(10,2,'REJECTED','Rejected',0,6,0),(16,2,'CREATED','CREATED',0,1,1),(17,2,'UNIT_TEST_READY','UNIT TEST READY',0,2,1),(18,2,'REQUEST_FOR_APPROVAL','REQUEST FOR APPROVAL',0,3,1),(19,2,'UNIT_TEST_COMPLETE','UNIT TEST COMPLETE',0,4,1),(20,2,'APPROVED_FOR_PRODUCTION','APPROVED FOR PRODUCTION',0,5, 1),(21,2,'REJECTED_FOR_PRODUCTION','REJECTED FOR PRODUCTION', 0,6,1),(22,2,'DEACTIVATED','DEACTIVATED',0,7,1);

--
-- Table structure for table appgroup_jobs_queue
--

DROP TABLE IF EXISTS appgroup_jobs_queue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE appgroup_jobs_queue_seq;

CREATE TABLE appgroup_jobs_queue (
  queueId int NOT NULL DEFAULT NEXTVAL ('appgroup_jobs_queue_seq'),
  idAppGroup int NOT NULL,
  uniqueId varchar(2000) NOT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  processId int DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  PRIMARY KEY (queueId)
) ;

--
-- Table structure for table appgroup_jobs_tracking
--

DROP TABLE IF EXISTS appgroup_jobs_tracking;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE appgroup_jobs_tracking_seq;

CREATE TABLE appgroup_jobs_tracking (
  id int NOT NULL DEFAULT NEXTVAL ('appgroup_jobs_tracking_seq'),
  idAppGroup int NOT NULL,
  uniqueId varchar(2000) NOT NULL,
  idApp int NOT NULL,
  validation_uniqueId varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table application
--

DROP TABLE IF EXISTS application;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE application (
  id int NOT NULL,
  name varchar(255) DEFAULT NULL,
  type varchar(255) DEFAULT NULL,
  output_type varchar(255) DEFAULT 'HBASE',
  output_path varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table column_profile_master_table
--

DROP TABLE IF EXISTS column_profile_master_table;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE column_profile_master_table_seq;

CREATE TABLE column_profile_master_table (
  row_id int NOT NULL DEFAULT NEXTVAL ('column_profile_master_table_seq'),
  domain_id int NOT NULL,
  schemaName varchar(500) DEFAULT NULL,
  idData int NOT NULL,
  template_name varchar(1000) NOT NULL,
  table_name varchar(10000) NOT NULL,
  column_name varchar(200) DEFAULT NULL,
  PRIMARY KEY (row_id)
) ;

--
-- Table structure for table component
--

DROP TABLE IF EXISTS component;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE component_seq;

CREATE TABLE component (
  row_id int NOT NULL DEFAULT NEXTVAL ('component_seq'),
  component_name varchar(255) DEFAULT NULL,
  component_title varchar(255) DEFAULT NULL,
  component_type smallint NOT NULL,
  module_row_id int DEFAULT NULL,
  http_url varchar(1000) DEFAULT NULL,
  active_flag smallint DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_component UNIQUE (component_name,component_type)
)  ;

ALTER SEQUENCE component_seq RESTART WITH 118;

--
-- Dumping data for table component
--

/*!40000 ALTER TABLE component DISABLE KEYS */;
INSERT INTO component VALUES (1,'dataConnection_View','DataConnection View',0,1,'/dataConnectionView', 1),(2,'edit_Schema','DataConnection Edit',0,1,'/editSchema',1),(3,'delete_Schema','DataConnection Delete',0,1,'/deleteSchema', 1),(4,'create_Schema','Add New DataConnection',0,1,'/createSchema', 1),(5,'add_New_Batch','Add New Batch DataConnection',0,1,'/addNewBatch', 1),(6,'datatemplate_view','View Data Template',0,2,'/datatemplateview', 1),(7,'list_Data_View','List Template Columns',0,2,'/listdataview', 1),(8,'edit_DataTemplate','Data Template Edit',0,2,'/editDataTemplate', 1),(9,'delete_DataTemplate','Data Template  Delete',0,2,'/deletedatasource', 1),(10,'data_Template_Add_New','Add New Data Template',0,2,'/dataTemplateAddNew', 1),(11,'derived_Template_Add_New','Add New Derived Template',0,2,'/derivedTemplateAddNew', 1),(12,'view_extendedTemplate','View Extend Template',0,2,'/extendTemplateView', 1),(13,'extend_Template_View','View Extend Template',0,3,'/index', 1),(14,'create_derivedColumn','Add Derived Column',0,3,'/createcolumn', 1),(15,'delete_ExtendedTemplate','Delete Extended Template',0,3,'/deleteTemp', 1),(16,'add_New_Extend_Template','Add New Extend Template',0,3,'/addNewExtendTemplate', 1),(17,'view_Rules','View Rules',0,3,'/viewRules', 1),(18,'edit_Rule','Edit Rules',0,3,'/editExtendTemplateRule', 1),(19,'add_New_Rule','Add New Rules',0,3,'/addNewRule', 1),(20,'advanced_Rules_Template_View','Advance Rules',0,3,'/advancedRulesTemplateView', 1),(21,'advanced_rulesList','View Advance Rules',0,3,'/advancedRulesTemplateView', 1),(22,'view_Global_Rules','View Global Rules',0,3,'/viewGlobalRules', 1),(23,'edit_Global_Rule','Edit Global Rules',0,8,'/editGlobalRule', 1),(24,'Add_New_Rule_Global','Add New Rule Global',0,8,'/AddNewRuleGlobal', 1),(25,'view_synonyms','Synonyms Library',0,8,'/viewsynonyms', 1),(26,'ref_datatemplate_view','References View',0,8,'/refdatatemplateview', 1),(27,'ref_data','View Reference',0,8,'/refData', 1),(28,'add_References','Add Internal References',0,8,'/addReferences', 1),(29,'dataTemplate_Add_New','Add External References',0,8,'/dataTemplateAddNew', 1),(30,'global_Threshold','Add Global Thresholds',0,8,'/globalThreshold', 1),(31,'validation_Check_View','View Validation',0,4,'/validationCheck_View', 1),(32,'Rules_Catalog','Rules Catalog',0,4,'/getRuleCatalog', 1),(33,'ValidationCheck_Customize','Customize Validation Check',0,4,'/customizeValidation', 1),(34,'ValidationCheck_Source','ValidationCheck Source',0,4,'/dataSourceDisplayAllView', 1),(35,'Create_Validation_Check','Create Validation',0,4,'/dataApplicationCreateView', 1),(36,'Submit_FileMonitoring','Submit FileMonitoring',0,4,'/submitFileMonitoringCSV', 1),(37,'batch_Validation','Add Batch Validation',0,4,'/batchValidation', 1),(38,'list_Applications_View','Run',0,5,'/listApplicationsView', 1),(39,'run_AppGroup','Run AppGroup',0,5,'/listApplicationsView', 1),(40,'run_Schema','Run Schema',0,5,'/runAppGroup', 1),(41,'running_Jobs_View','View Job Status',0,5,'/runningJobsView', 1),(42,'view_Schedules','View Schedules',0,5,'/viewSchedules', 1),(43,'edit_Schedule','Edit Schedule',0,5,'/editSchedule', 1),(44,'delete_Schedule','Delete Schedule',0,5,'/deleteSchedule', 1),(45,'scheduled_Task','Add New Schedules',0,5,'/scheduledTask', 1),(46,'view_Triggers','View Triggers',0,5,'/viewTriggers', 1),(47,'delete_Trigger','Delete Trigger',0,5,'/deleteTrigger', 1),(48,'trigger_Task','Add New Triggers',0,5,'/triggerTask', 1),(49,'view_AppGroups','View App Groups',0,5,'/viewAppGroups', 1),(50,'customize_AppGroups','Customize AppGroups',0,5,'/customizeAppGroup', 1),(51,'add_AppGroup','Add New App Groups',0,5,'/addAppGroup', 1),(52,'dashboard_View','Quality',0,6,'/dashboard_View', 1),(53,'Count_Reasonability','Count reasonability',0,6,'/dashboard_table', 1),(54,'Approval_process','Approval process',0,6,'/reviewProcessController', 1),(55,'microsegment_validity','Microsegment Validity',0,6,'/validity', 1),(56,'Duplicate_check','Duplicate check',0,6,'/dupstats', 1),(57,'Data_Completeness','Data Completeness',0,6,'/nullstats', 1),(58,'Conformity','Conformity',0,6,'/badData', 1),(59,'Custom_Rules','Custom Rules',0,6,'/sqlRules', 1),(60,'Data_Drift','Data Drift',0,6,'/stringstats', 1),(61,'Distribution_Check','Distribution Check',0,6,'/numericstats', 1),(62,'Record_Anomaly','Record Anomaly',0,6,'/recordAnomaly', 1),(63,'Sequence','Sequence',0,6,'/timelinessCheck', 1),(64,'Exceptions','Exceptions',0,6,'/exceptions', 1),(65,'RootCause-Analysis','RootCause Analysis',0,6,'/rootCauseAnalysis', 1),(66,'profile_DataTemplate_View','Profile',0,6,'/profileDataTemplateView', 1),(67,'View_Profiling result','Profiling result view',0,6,'/dataProfiling_View', 1),(68,'get_DataMatching_Results','Key Measurement Matching',0,6,'/getDataMatchingResults', 1),(69,'view_DataMatching_results','view DataMatching results',0,6,'/getMatchTablesData', 1),(70,'get_RollData_Matching_Results','Roll DataMatching',0,6,'/getRollDataMatchingResults', 1),(71,'view_Roll_DataMatching_results','view Roll DataMatching results',0,6,'/getRollDataMatchTablesData', 1),(72,'Statistical_Matching_Result_View','FingerPrint Matching',0,6,'/StatisticalMatchingResultView', 1),(73,'Schema_Matching_Result_View','Schema Matching',0,6,'/SchemaMatchingResultView', 1),(74,'show_Schemamatching','show schema matching',0,6,'/showSchemaMatchingData', 1),(75,'file_Monitoring_View','File Monitoring',0,6,'/fileMonitoringView', 1),(76,'file_Monitoring_Results','file Monitoring Results',0,6,'/fileMonitorResults', 1),(77,'File_Management_Result_View','File Management',0,6,'/FileManagementResultView', 1),(78,'Model_Governance_Result_View','Model Governance',0,6,'/ModelGovernanceResultView', 1),(79,'Model_Governance_Dashboard_Result_View','Model Governance Dashboard',0,6,'/ModelGovernanceDashboardResultView', 1),(80,'download_Log_Files','Log Files',0,6,'/downloadLogFiles', 1),(81,'Dash_Configuration','Dash Configuration',0,6,'/dashConfiguration', 1),(82,'change_Password','Change Password',0,7,'/changePassword', 1),(83,'generate_SecureAPI','Generate API Token',0,7,'/generateSecureAPI', 1),(84,'migrate_database','migrate Database',0,7,'/migrateDatabase', 1),(85,'access_Controls','View Modules',0,7,'/accessControls', 1),(86,'role_Management','View Roles',0,7,'/roleManagement', 1),(87,'edit_role','Edit Role',0,7,'/editRoleModule', 1),(88,'add_New_Role','Add New Role',0,7,'/addNewRole', 1),(89,'view_Users','View Users',0,7,'/viewUsers', 1),(90,'add_New_User','Add New User',0,7,'/addNewUser', 1),(91,'domain_ViewList','Domain Library',0,7,'/domainViewList', 1),(92,'view_Project','View Project',0,7,'/viewProject', 1),(93,'edit_project','Edit Project',0,7,'/editProject', 1),(94,'delete_Project','Delete Project',0,7,'/deleteProject', 1),(95,'add_New_Project','Add New Project',0,7,'/addNewProject', 1),(96,'group_Role_Map_Add_new','Map ADGroup & Role',0,7,'/groupRoleMapAddnew', 1),(97,'add_New_Location','Add New Location',0,7,'/addNewLocation', 1),(98,'map_Location_And_Validation','Map Location & Validation',0,7,'/mapLocationAndValidation', 1),(99,'license_Information','License Information',0,7,'/licenseInformation', 1),(100,'get_ImportUI','Import',0,7,'/getImportUI', 1),(101,'Submit_Import_Form','Submit Import Form',0,7,'/submitImportUiForm', 1),(102,'get_ExportUI','Export',0,7,'/getExportUI', 1),(103,'export_Connection_View','Export Connection View',0,7,'/exportDataConnectionView', 1),(104,'export_CSVFile','Export CSV File',0,7,'/exportCSVFileData', 1),(105,'export_Template_View','Export Template View',0,7,'/exportDataTemplateView', 1),(106,'export_ValidationCheck_View','Export ValidationCheck View',0,7,'/exportValidationCheck', 1),(107,'Features_Access_Control','Features Access Control',0,7,'/componentAccessControl', 1),(108,'view_ApplicationSettings','View Application Settings',0,11,'/applicationSettingsView', 1),(109,'Notification_Setup_Subcriptions','Notification View',0,7,'/notificationView', 1),(110,'Exception_Data_Report','Exception Data Report',0,7,'/ManageExceptionDataReport', 1),(111,'delete_Users','Delete Users',0,7,'/deleteUserModule', 1),(112,'delete_Roles','Delete Roles',0,7,'/deleteRoleModule', 1),(113,'edit_Domain','Edit Domain',0,7,'/mainDomainHandler', 1),(114,'delete_Domain','Delete Domain',0,7,'/mainDomainHandler', 1),(115,'dimension_Library','Dimension Library',0,7,'/dimensionViewList', 1),(116,'edit_Dimension','Edit Dimension',0,7,'/saveDimensionRecord', 1),(117,'delete_Dimension','Delete Dimension',0,7,'/deleteDimensionRecord', 1);
/*!40000 ALTER TABLE component ENABLE KEYS */;

--
-- Table structure for table component_access
--

DROP TABLE IF EXISTS component_access;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE component_access_seq;

CREATE TABLE component_access (
  row_id int NOT NULL DEFAULT NEXTVAL ('component_access_seq'),
  role_row_id int NOT NULL,
  component_row_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_component_access UNIQUE (role_row_id,component_row_id)
)  ;

ALTER SEQUENCE component_access_seq RESTART WITH 137;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table component_access
--

/*!40000 ALTER TABLE component_access DISABLE KEYS */;
INSERT INTO component_access VALUES (26,1,1),(49,1,2),(36,1,3),(20,1,4),(3,1,5),(28,1,6),(69,1,7),(43,1,8),(32,1,9),(31,1,10),(38,1,11),(99,1,12),(55,1,13),(19,1,14),(33,1,15),(4,1,16),(104,1,17),(47,1,18),(8,1,19),(13,1,20),(12,1,21),(100,1,22),(44,1,23),(9,1,24),(106,1,25),(78,1,26),(77,1,27),(11,1,28),(27,1,29),(65,1,30),(95,1,31),(81,1,32),(93,1,33),(94,1,34),(21,1,35),(90,1,36),(15,1,37),(68,1,38),(83,1,39),(84,1,40),(82,1,41),(105,1,42),(48,1,43),(35,1,44),(85,1,45),(107,1,46),(37,1,47),(92,1,48),(96,1,49),(22,1,50),(2,1,51),(24,1,52),(18,1,53),(14,1,54),(71,1,55),(42,1,56),(29,1,57),(17,1,58),(23,1,59),(30,1,60),(39,1,61),(76,1,62),(87,1,63),(50,1,64),(80,1,65),(75,1,66),(101,1,67),(61,1,68),(98,1,69),(64,1,70),(103,1,71),(89,1,72),(86,1,73),(88,1,74),(59,1,75),(58,1,76),(57,1,77),(74,1,78),(73,1,79),(41,1,80),(25,1,81),(16,1,82),(60,1,83),(72,1,84),(1,1,85),(79,1,86),(46,1,87),(7,1,88),(108,1,89),(10,1,90),(40,1,91),(102,1,92),(45,1,93),(34,1,94),(6,1,95),(66,1,96),(5,1,97),(70,1,98),(67,1,99),(63,1,100),(91,1,101),(62,1,102),(51,1,103),(52,1,104),(53,1,105),(54,1,106),(56,1,107),(97,1,108),(128,1,109),(129,1,110),(130,1,111),(131,1,112),(132,1,113),(133,1,114),(134,1,115),(135,1,116),(136,1,117);
/*!40000 ALTER TABLE component_access ENABLE KEYS */;

--
-- Table structure for table dashboard_check_component_list
--

DROP TABLE IF EXISTS dashboard_check_component_list;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dashboard_check_component_list_seq;

CREATE TABLE dashboard_check_component_list (
  componentId int NOT NULL DEFAULT NEXTVAL ('dashboard_check_component_list_seq'),
  checkName varchar(2500) DEFAULT NULL,
  description varchar(2500) DEFAULT NULL,
  component varchar(2500) DEFAULT NULL,
  entity_name varchar(2500) DEFAULT NULL,
  technical_name varchar(2500) DEFAULT NULL,
  technical_check_value varchar(2500) DEFAULT NULL,
  technical_result_name varchar(2500) DEFAULT NULL,
  PRIMARY KEY (componentId)
)  ;

ALTER SEQUENCE dashboard_check_component_list_seq RESTART WITH 19;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table dashboard_check_component_list
--

/*!40000 ALTER TABLE dashboard_check_component_list DISABLE KEYS */;
INSERT INTO dashboard_check_component_list VALUES (1,'Null Check','Check number of Nulls values in a column','Essential Check','listApplications','nonNullCheck','Y','DQ_Completeness'),(2,'MicroSegment Based Null Check','Check number of Nulls values in a column per microsegment','Essential Check','listApplications','dGroupNullCheck','Y','DQ_Completeness'),(3,'Default Value check','Check if only Default values specified are present in column','Essential Check','listApplications','defaultCheck','Y','DQ_DefaultCheck'),(4,'Length Check','Check if column data is only of specified length','Essential Check','listApplications','lengthCheck','Y','DQ_LengthCheck'),(5,'Bad Data Check','Check if column has bad data','Essential Check','listApplications','badData','Y','DQ_Bad_Data'),(6,'Date Rule Check','Perform Date comparision checks ','Essential Check','listApplications','dateRuleCheck','Y','DQ_DateRuleCheck'),(7,'Microsegment Based Date Rule Check','Perform Date comparision checks per microsegment','Essential Check','listApplications','dGroupDateRuleCheck','Y','DQ_DateRuleCheck'),(8,'Pattern Check','Check if column data is matching the specified pattern','Essential Check','listApplications','patternCheck','Y','DQ_Pattern_Data'),(9,'Duplicate Checks ','Check if column has duplicate data either primary keys or selected fields','Essential Check','listDFTranRule','dupRow','Y','DQ_Uniqueness -Primary Keys, DQ_Uniqueness -Seleted Fields'),(10,'Custom Rule Checks ','Execute and validate custom rules','Essential Check','listApplications','applyRules','Y','DQ_Sql_Rule,DQ_Rules'),(11,'Record Count Check','Check if Record count of data has anomaly','Essential Check','listApplications','recordCountAnomaly','Y','DQ_Record Count Fingerprint'),(12,'Microsegment Based Record Count Check','Check if Record count of microsegment data has anomaly','Essential Check','listApplications','keyGroupRecordCountAnomaly','Y','DQ_Record Count Fingerprint'),(13,'Timeliness Check','Perform timeliness check','Advanced Check','listApplications','timelinessKeyCheck','Y','DQ_Timeliness'),(14,'Record Anomaly – Current Batch and Historical','Check if column data has anomaly','Advanced Check','listApplications','recordAnomalyCheck','Y','DQ_Record Anomaly'),(15,'Trend Check / Numerical Fingerprint ','Numerical column distribution check','Advanced Check','listApplications','numericalStatCheck','Y','DQ_Numerical Field Fingerprint'),(16,'Data Drift Check ','Check differences in Unique values of column','Advanced Check','listApplications','dataDriftCheck','Y','DQ_Data Drift'),(17,'Microsegment Based Data Drift Check','Check differences in Unique values in a column microsegment','Advanced Check','listApplications','dGroupDataDriftCheck','Y','DQ_Data Drift'),(18,'Derived columns','Execute and validate custom rules','Advanced Check','listApplications','applyDerivedColumns','Y','DQ_Sql_Rule,DQ_Rules');
/*!40000 ALTER TABLE dashboard_check_component_list ENABLE KEYS */;

--
-- Table structure for table dashboard_conn_app_list
--

DROP TABLE IF EXISTS dashboard_conn_app_list;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dashboard_conn_app_list_seq;

CREATE TABLE dashboard_conn_app_list (
  conn_app_id int NOT NULL DEFAULT NEXTVAL ('dashboard_conn_app_list_seq'),
  domainId int NOT NULL,
  projectId int NOT NULL,
  connectionId int NOT NULL,
  idApp int NOT NULL,
  datasource varchar(2500) DEFAULT NULL,
  source varchar(2500) DEFAULT NULL,
  fileName varchar(2500) DEFAULT NULL,
  PRIMARY KEY (conn_app_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table dashboard_project_color_grade
--

DROP TABLE IF EXISTS dashboard_project_color_grade;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dashboard_project_color_grade_seq;

CREATE TABLE dashboard_project_color_grade (
  gradeId int NOT NULL DEFAULT NEXTVAL ('dashboard_project_color_grade_seq'),
  domainId int NOT NULL,
  projectId int NOT NULL,
  color varchar(2000) DEFAULT NULL,
  logic varchar(2000) DEFAULT NULL,
  color_percentage decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (gradeId)
) ;

--
-- Table structure for table dashboard_project_conn_list
--

DROP TABLE IF EXISTS dashboard_project_conn_list;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dashboard_project_conn_list_seq;

CREATE TABLE dashboard_project_conn_list (
  id int NOT NULL DEFAULT NEXTVAL ('dashboard_project_conn_list_seq'),
  domainId int NOT NULL,
  projectId int NOT NULL,
  connectionId int NOT NULL,
  displayName varchar(200) DEFAULT NULL,
  displayOrder int DEFAULT NULL,
  PRIMARY KEY (id)
) ;

--
-- Table structure for table data_blend
--

DROP TABLE IF EXISTS data_blend;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE data_blend_seq;

CREATE TABLE data_blend (
  id int NOT NULL DEFAULT NEXTVAL ('data_blend_seq'),
  datasource_id int NOT NULL,
  expression varchar(255) NOT NULL,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ;

CREATE INDEX datasource_id ON data_blend (datasource_id);

--
-- Table structure for table data_domain
--

DROP TABLE IF EXISTS data_domain;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE data_domain_seq;

CREATE TABLE data_domain (
  row_id int NOT NULL DEFAULT NEXTVAL ('data_domain_seq'),
  name varchar(100) NOT NULL,
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_data_domain UNIQUE (name)
)  ;

ALTER SEQUENCE data_domain_seq RESTART WITH 5;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table data_domain
--

/*!40000 ALTER TABLE data_domain DISABLE KEYS */;
INSERT INTO data_domain VALUES (2,'Customer',1),(3,'Product', 1),(4,'Transaction', 1);
/*!40000 ALTER TABLE data_domain ENABLE KEYS */;

--
-- Table structure for table data_source
--

DROP TABLE IF EXISTS data_source;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE data_source_seq;

CREATE TABLE data_source (
  id int NOT NULL DEFAULT NEXTVAL ('data_source_seq'),
  name varchar(255) NOT NULL,
  type varchar(500) DEFAULT NULL,
  FORMAT varchar(500) DEFAULT NULL,
  url varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  filter varchar(1000) NOT NULL DEFAULT '',
  app_id int NOT NULL,
  seq int NOT NULL DEFAULT '0',
  checkSource varchar(200) DEFAULT 'normal',
  RowAddSource varchar(200) DEFAULT NULL,
  query varchar(10) DEFAULT NULL,
  incrementalType varchar(10) DEFAULT NULL,
  whereCondition varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
 ,
  CONSTRAINT data_source_ibfk_1 FOREIGN KEY (app_id) REFERENCES application (id)
)  ;

ALTER SEQUENCE data_source_seq RESTART WITH 241;

CREATE INDEX app_id ON data_source (app_id);

--
-- Table structure for table data_filter
--

DROP TABLE IF EXISTS data_filter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE data_filter_seq;

CREATE TABLE data_filter (
  id int NOT NULL DEFAULT NEXTVAL ('data_filter_seq'),
  expression text NOT NULL,
  datasource_id int NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT data_filter_ibfk_1 FOREIGN KEY (datasource_id) REFERENCES data_source (id)
) ;

CREATE INDEX index_data_filter ON data_filter (datasource_id);

--
-- Table structure for table databuck_activity_urls
--

DROP TABLE IF EXISTS databuck_activity_urls;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE databuck_activity_urls_seq;

CREATE TABLE databuck_activity_urls (
  row_id int NOT NULL DEFAULT NEXTVAL ('databuck_activity_urls_seq'),
  activity_title varchar(255) DEFAULT NULL,
  http_url varchar(255) DEFAULT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_http_url UNIQUE (http_url)
)  ;

ALTER SEQUENCE databuck_activity_urls_seq RESTART WITH 110;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table databuck_activity_urls
--

/*!40000 ALTER TABLE databuck_activity_urls DISABLE KEYS */;
INSERT INTO databuck_activity_urls VALUES (1,'DataConnection View','/dataConnectionView'),(2,'Get Application Ids','/validationIdApp'),(3,'User Activity Log','/accessLog'),(4,'DataConnection Edit','/editSchema'),(5,'DataConnection Delete','/deleteSchema'),(6,'Add New DataConnection','/createSchema'),(7,'Add New Batch DataConnection','/addNewBatch'),(8,'View Data Template','/datatemplateview'),(9,'List Template Columns','/listdataview'),(10,'Data Template Edit','/editDataTemplate'),(11,'Data Template  Delete','/deletedatasource'),(12,'Add New Data Template','/dataTemplateAddNew'),(13,'Add New Derived Template','/derivedTemplateAddNew'),(14,'View Extend Template','/extendTemplateView'),(15,'View Extend Template','/index'),(16,'Add Derived Column','/createcolumn'),(17,'Delete Extended Template','/deleteTemp'),(18,'Add New Extend Template','/addNewExtendTemplate'),(19,'View Rules','/viewRules'),(20,'Edit Rules','/editExtendTemplateRule'),(21,'Add New Rules','/addNewRule'),(22,'View Advance Rules','/advancedRulesTemplateView'),(23,'View Global Rules','/viewGlobalRules'),(24,'Edit Global Rules','/editGlobalRule'),(25,'Add New Rule Global','/AddNewRuleGlobal'),(26,'Synonyms Library','/viewsynonyms'),(27,'References View','/refdatatemplateview'),(28,'View Reference','/refData'),(29,'Add Internal References','/addReferences'),(30,'Add Global Thresholds','/globalThreshold'),(31,'View Validation','/validationCheck_View'),(32,'Rules Catalog','/getRuleCatalog'),(33,'Customize Validation Check','/customizeValidation'),(34,'ValidationCheck Source','/dataSourceDisplayAllView'),(35,'Create Validation','/dataApplicationCreateView'),(36,'Submit FileMonitoring','/submitFileMonitoringCSV'),(37,'Add Batch Validation','/batchValidation'),(38,'Run Validation or AppGroup','/listApplicationsView'),(39,'Run Schema','/runAppGroup'),(40,'View Job Status','/runningJobsView'),(41,'View Schedules','/viewSchedules'),(42,'Edit Schedule','/editSchedule'),(43,'Delete Schedule','/deleteSchedule'),(44,'Add New Schedules','/scheduledTask'),(45,'View Triggers','/viewTriggers'),(46,'Delete Trigger','/deleteTrigger'),(47,'Add New Triggers','/triggerTask'),(48,'View App Groups','/viewAppGroups'),(49,'Customize AppGroups','/customizeAppGroup'),(50,'Add New App Groups','/addAppGroup'),(51,'Data Quality view','/dashboard_View'),(52,'Count reasonability','/dashboard_table'),(53,'Approval process','/reviewProcessController'),(54,'Microsegment Validity','/validity'),(55,'Duplicate check','/dupstats'),(56,'Data Completeness','/nullstats'),(57,'Conformity','/badData'),(58,'Custom Rules','/sqlRules'),(59,'Data Drift','/stringstats'),(60,'Distribution Check','/numericstats'),(61,'Record Anomaly','/recordAnomaly'),(62,'Sequence','/timelinessCheck'),(63,'Exceptions','/exceptions'),(64,'RootCause Analysis','/rootCauseAnalysis'),(65,'DT Profile view','/profileDataTemplateView'),(66,'Profiling result view','/dataProfiling_View'),(67,'Key Measurement Matching','/getDataMatchingResults'),(68,'Roll DataMatching','/getRollDataMatchingResults'),(69,'view Roll DataMatching results','/getRollDataMatchTablesData'),(70,'FingerPrint Matching','/StatisticalMatchingResultView'),(71,'Schema Matching','/SchemaMatchingResultView'),(72,'show schema matching','/showSchemaMatchingData'),(73,'File Monitoring view','/fileMonitoringView'),(74,'file Monitoring Results','/fileMonitorResults'),(75,'File Management','/FileManagementResultView'),(76,'Model Governance','/ModelGovernanceResultView'),(77,'Model Governance Dashboard view','/ModelGovernanceDashboardResultView'),(78,'Log Files','/downloadLogFiles'),(79,'Dash Configuration','/dashConfiguration'),(80,'Change Password','/changePassword'),(81,'Generate API Token','/generateSecureAPI'),(82,'migrate Database','/migrateDatabase'),(83,'View Modules','/accessControls'),(84,'View Roles','/roleManagement'),(85,'Edit Role','/editRoleModule'),(86,'Add New Role','/addNewRole'),(87,'View Users','/viewUsers'),(88,'Add New User','/addNewUser'),(89,'Domain Library','/domainViewList'),(90,'View Project','/viewProject'),(91,'Edit Project','/editProject'),(92,'Delete Project','/deleteProject'),(93,'Add New Project','/addNewProject'),(94,'Map ADGroup & Role','/groupRoleMapAddnew'),(95,'Add New Location','/addNewLocation'),(96,'Map Location & Validation','/mapLocationAndValidation'),(97,'License Information','/licenseInformation'),(98,'Import','/getImportUI'),(99,'Submit Import Form','/submitImportUiForm'),(100,'Export','/getExportUI'),(101,'Export Connection View','/exportDataConnectionView'),(102,'Export CSV File','/exportCSVFileData'),(103,'Export Template View','/exportDataTemplateView'),(104,'Export ValidationCheck View','/exportValidationCheck'),(105,'Features Access Control','/databuck_activity_urlsAccessControl'),(106,'View Application Settings','/applicationSettingsView'),(107,'Exception Data Report','/ManageExceptionDataReport'),(108,'Notification View','/notificationView'),(109,'Login Group Mapping','/loginGroupMapping');
/*!40000 ALTER TABLE databuck_activity_urls ENABLE KEYS */;

--
-- Table structure for table databuck_properties_master
--

DROP TABLE IF EXISTS databuck_properties_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE databuck_properties_master_seq;

CREATE TABLE databuck_properties_master (
  property_category_id int NOT NULL DEFAULT NEXTVAL ('databuck_properties_master_seq'),
  property_category_name varchar(1000) NOT NULL,
  description text,
  created_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (property_category_id),
  CONSTRAINT property_category_name UNIQUE (property_category_name)
)  ;

ALTER SEQUENCE databuck_properties_master_seq RESTART WITH 14;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table databuck_properties_master
--

/*!40000 ALTER TABLE databuck_properties_master DISABLE KEYS */;
INSERT INTO databuck_properties_master VALUES (1,'appdb',NULL,'2022-07-19 17:57:22'),(2,'resultsdb',NULL,'2022-07-19 17:57:22'),(3,'activedirectory',NULL,'2022-07-19 17:57:22'),(4,'cluster',NULL,'2022-07-19 17:57:22'),(5,'license',NULL,'2022-07-19 17:57:22'),(6,'mongodb',NULL,'2022-07-19 17:57:22'),(7,'dbdependency',NULL,'2022-07-19 17:57:22'),(8,'dynamicvariable',NULL,'2022-07-19 17:57:22'),(9,'integration',NULL,'2022-07-19 17:57:23'),(10,'cdp',NULL,'2022-07-19 17:57:26'),(11,'gcp',NULL,'2022-07-19 17:57:26'),(12,'azure',NULL,'2022-07-19 17:57:26'),(13,'mapr',NULL,'2022-07-19 17:57:26');
/*!40000 ALTER TABLE databuck_properties_master ENABLE KEYS */;

--
-- Table structure for table databuck_property_details
--

DROP TABLE IF EXISTS databuck_property_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE databuck_property_details_seq;

CREATE TABLE databuck_property_details (
  property_id int NOT NULL DEFAULT NEXTVAL ('databuck_property_details_seq'),
  property_category_id int NOT NULL,
  property_name varchar(2500) NOT NULL,
  property_value text,
  description text,
  is_mandatory_field varchar(10) DEFAULT 'N',
  is_password_field varchar(10) DEFAULT 'N',
  is_value_encrypted varchar(10) DEFAULT 'N',
  property_default_value text,
  property_data_type varchar(500) DEFAULT NULL,
  prop_requires_restart varchar(10) DEFAULT 'N',
  last_updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (property_id),
  CONSTRAINT databuck_property_details_ibfk_1 FOREIGN KEY (property_category_id) REFERENCES databuck_properties_master (property_category_id)
)  ;

ALTER SEQUENCE databuck_property_details_seq RESTART WITH 210;

CREATE INDEX property_category_id ON databuck_property_details (property_category_id);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table databuck_property_details
--

/*!40000 ALTER TABLE databuck_property_details DISABLE KEYS */;
INSERT INTO databuck_property_details VALUES (5,1,'isRuleCatalogDiscovery','N','Property to enable or disable RuleCatalog Discovery','N','N','N','N','string','Y','2022-07-19 17:57:22'),(6,1,'isTestApprovalRequired','N','Property to enable approval to execute unit testing validation','N','N','N','N','string','Y','2022-07-19 17:57:22'),(7,1,'isURLSecurityRequired','N','Property to enable security for all the databuck UI Urls','N','N','N','N','string','N','2022-07-19 17:57:22'),(8,1,'isTemplateUpdationSupported','N','Property to mention template updation supported or not when created using Rest API','N','N','N','N','string','N','2022-07-19 17:57:22'),(9,1,'dynamicVariableEnabled','N','Property to enable dynamic variable logic for cisco','N','N','N','N','string','Y','2022-07-19 17:57:22'),(10,1,'exportImportMode','N','Property to enable/disable Import Export feature','N','N','N','N','string','N','2022-07-19 17:57:22'),(11,1,'snowflake.securemode','N','Property to enable/disable snowflake secure mode','N','N','N','N','string','N','2022-07-19 17:57:22'),(12,1,'maxActiveJobCount','4','Property to specify maximum Active spark jobs to run in parallel','N','N','N','16','string','N','2022-07-19 17:57:22'),(13,1,'dq.unsupported.datatypes','array,map,tuple','Property to write comma separated list of unsupported datatypes in table metadata','N','N','N','','string','N','2022-07-19 17:57:22'),(14,1,'csvWriteLimit','100000','Propety to specify number of output/result records written into csv file','N','N','N','100','string','N','2022-07-19 17:57:22'),(15,1,'databuck.result.fileType','csv','Property to specify the type in which file has to be saved [Eg. parquet, csv]','N','N','N','csv','string','N','2022-07-19 17:57:22'),(16,1,'custom_sampling_count','10000','Custom Sampling count for analysis','N','N','N','10000','string','N','2022-07-19 17:57:22'),(17,1,'custom.execution.record.count','1000','Specify the number of records to be executed for test run','N','N','N','','string','N','2022-07-19 17:57:22'),(18,1,'RuleCatalogApproverRole','','Property to configure role name which can approve rule catalog','N','N','N','N','string','N','2022-07-19 17:57:22'),(19,1,'serviceAuthenticationType','','Property to select authentication type for Rest APIs (token or ldap)','N','N','N','token','string','N','2022-07-19 17:57:22'),(20,1,'serviceLdapGroup','','Property to specify the ldap group of the users who can access the REST API','N','N','N','','string','N','2022-07-19 17:57:22'),(21,1,'application.server.path','','Property to specify the tomcat bin folder, used to restart tomcat from UI','N','N','N','','string','N','2022-07-19 17:57:22'),(22,1,'file.pattern.regex','N','Property to enable file name pattern detection based on regex','N','N','N','N','string','N','2022-07-19 17:57:22'),(23,1,'filemonitoring.fullfile.check','N','Property to enable/disable fileMonitoring full file data check','N','N','N','N','string','N','2022-07-19 17:57:22'),(24,1,'datadrift.potentialduplicates.enabled','N','Property to enable or disable detection of potential duplicates in datadrift','N','N','N','N','string','N','2022-07-19 17:57:22'),(25,1,'advancedrules.reasonability.disable','N','Property to disable reasonability advanced rules generation during template analysis','N','N','N','N','string','N','2022-07-19 17:57:22'),(26,1,'advancedrules.colrltnship.uniquedatacount','50','Property to specify eligible unique data count of columns to generate column relationship rules.','N','N','N','50','string','N','2022-07-19 17:57:22'),(27,1,'rootcause.analysis.check','Y','Property to enable or disable RootCause Analysis check','N','N','N','N','string','N','2022-07-19 17:57:22'),(28,1,'rootcause.analysis.threshold','30','Root cause analysis threshold value','N','N','N','6','string','N','2022-07-19 17:57:22'),(31,1,'gssJass_File_Path','','Property to specify the jass file path to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:22'),(33,1,'aging.exceptionData.limit','5000000','Property to specify a record limit upto which aging report generation is allowed','N','N','N','5000000','string','N','2022-07-19 17:57:22'),(34,1,'aging.fullLoad.exceptionDataPath','','Property to specify the location to store the aging exception report for full Load Data','N','N','N','','string','N','2022-07-19 17:57:22'),(35,1,'s3CsvPath','','Property to mention the s3 path where result files will be stored, when deployment mode is s3','N','N','N','','string','N','2022-07-19 17:57:22'),(36,1,'s3.bucketname','','aws s3 bucket name','N','N','N','','string','N','2022-07-19 17:57:22'),(37,1,'s3.aws.accessKey','','Aws accesskey to connect to s3','N','N','N','','string','Y','2022-07-19 17:57:22'),(38,1,'s3.aws.secretKey','','aws secret key to connect to s3','N','Y','Y','','string','Y','2022-07-19 17:57:22'),(39,1,'s3.decrypt.passphrase','','Property to mention the passphrase used to decrypt the encrypted files','N','Y','Y','','string','N','2022-07-19 17:57:22'),(40,1,'jbpm.workflow.url','','Property to mention jbpm workflow rest url','N','N','N','','string','N','2022-07-19 17:57:22'),(41,1,'jbpm.workflow.user','','Property to mention jbpm workflow rest url username','N','N','N','','string','N','2022-07-19 17:57:22'),(42,1,'jbpm.workflow.password','','Property to mention jbpm workflow rest url password','N','Y','Y','','string','N','2022-07-19 17:57:22'),(43,1,'ruledefectcode.rest.url','','Property to hold the external rest api url for getting the defect code of rule','N','N','N','','string','N','2022-07-19 17:57:22'),(44,1,'colibra.metadata.url','','Colibra url to get the metadata from steel - Wellsfargo specific','N','N','N','','string','N','2022-07-19 17:57:22'),(45,1,'aver.report.link','http://host:port/home/loading?token=','Property to mention aver report link','N','N','N','','string','N','2022-07-19 17:57:22'),(46,1,'date.format.timeliness','dd-MMM-yyyy','date format for timeliness','N','N','N','','string','N','2022-07-19 17:57:22'),(47,1,'date.format.Incremental','yyyy-MM-dd HH:mm:ss','date format for incremental load ','N','N','N','','string','N','2022-07-19 17:57:22'),(48,1,'date.format.Incremental.db','MM/dd/yyyy','date format for incremental load in database','N','N','N','','string','N','2022-07-19 17:57:22'),(49,1,'number.Format','US','number format','N','N','N','','string','N','2022-07-19 17:57:22'),(50,1,'number.FormatCol','','number format col','N','N','N','','string','N','2022-07-19 17:57:22'),(51,1,'match.string','[a-zA-Z]','regex for string','N','N','N','','string','N','2022-07-19 17:57:22'),(52,1,'match.dateRegexFormate','d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])*,(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])-d{4},(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/d{4},[0-9]{4}-(0[1-9]|1[0-2])-([0-2][0-9]|[3][0-1]) [0-2]?[0-9]:[0-6]?[0-9]:[0-6]?[0-9]','date regex format','N','N','N','','string','N','2022-07-19 17:57:22'),(53,1,'match.dateRegexFormateD','^(?:(?:31(/|-|.)(?:0?[13578]|1[02]|(?:Jan|Mar|May|Jul|Aug|Oct|Dec)))1|(?:(?:29|30)(/|-|.)(?:0?[1,3-9]|1[0-2]|(?:Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))2))(?:(?:1[6-9]|[2-9]d)?d{2})$|^(?:29(/|-|.)(?:0?2|(?:Feb))3(?:(?:(?:1[6-9]|[2-9]d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1d|2[0-8])(/|-|.)(?:(?:0?[1-9]|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep))|(?:1[0-2]|(?:Oct|Nov|Dec)))4(?:(?:1[6-9]|[2-9]d)?d{2})$','date Regex Format','N','N','N','','string','N','2022-07-19 17:57:22'),(54,1,'match.numberRegex','^d+([,.]d+)?$','number Regex','N','N','N','','string','N','2022-07-19 17:57:22'),(55,1,'match.numberRegexUS','^-?(0|[1-9][0-9]{0,2}(?:(,[0-9]{3})*|[0-9]*))(.[0-9]+){0,1}$','number Regex US','N','N','N','','string','N','2022-07-19 17:57:22'),(56,1,'match.numberRegexEU','^-?(0|[1-9][0-9]{0,2}(?:(.[0-9]{3})*|[0-9]*))(,[0-9]+){0,1}$','number regex EU','N','N','N','','string','N','2022-07-19 17:57:22'),(57,1,'match.numberRegexUSD','^-?(0|[1-9][0-9]{0,2}(?:(,[0-9]{3})*|[0-9]*))(.[0-9]+){0,1}$','number Regex for US','N','N','N','','string','N','2022-07-19 17:57:22'),(58,1,'match.numberRegexEUD','^-?(0|[1-9][0-9]{0,2}(?:(.[0-9]{3})*|[0-9]*))(,[0-9]+){0,1}$','number Regex for EU','N','N','N','','string','N','2022-07-19 17:57:22'),(59,1,'killAutomatically','Y','Property to enable kill the overtimejob automatically','N','N','N','N','string','N','2022-07-19 17:57:22'),(60,1,'killTime','01:00','Property to mention duration of job considered overtime to kill job. Format HH:MM, it is duration not Time','N','N','N','01:00','string','N','2022-07-19 17:57:22'),(61,1,'retention.dataquality.days','80','Property to mention no of days of dataquality data has to retained','N','N','N','30','string','N','2022-07-19 17:57:22'),(62,1,'retention.datamatching.days','80','Property to mention no of days of datamatching data has to retained','N','N','N','30','string','N','2022-07-19 17:57:22'),(63,1,'SNSNotifications','N','Property to enable/disable SQS Notifications','N','N','N','N','string','N','2022-07-19 17:57:22'),(64,1,'sns.notifications.iamrole','N','Property to enable/disable SNS Notifications using IAMRole','N','N','N','N','string','N','2022-07-19 17:57:22'),(65,1,'sns.aws.topicARN','','Property to mention SNS Topic ARN','N','N','N','','string','N','2022-07-19 17:57:22'),(66,1,'sns.topic.region','','Property to mention SNS Topic region','N','N','N','','string','N','2022-07-19 17:57:22'),(67,1,'sns.aws.accessKey','','aws accesskey for sns','N','N','N','','string','N','2022-07-19 17:57:22'),(68,1,'sns.aws.secretKey','','aws secret key for sns','N','Y','Y','','string','N','2022-07-19 17:57:22'),(69,1,'sqs.notifications','N','Property to enable/disable SQS Notifications','N','N','N','N','string','N','2022-07-19 17:57:22'),(70,1,'sqs.notifications.queue.url','','Property to mention SQS queue Url','N','N','N','','string','N','2022-07-19 17:57:22'),(71,1,'sqs.notifications.queue.region','','Property to mention SQS queue region','N','N','N','','string','N','2022-07-19 17:57:22'),(72,1,'smtp_host','','host details for smtp - send email','N','N','N','','string','N','2022-07-19 17:57:22'),(73,1,'smtp_port','587','port details for smtp - send email','N','N','N','','string','N','2022-07-19 17:57:22'),(74,1,'smtp_mode','','Property to specify the smtp mode auth/noAuth','N','N','N','noAuth','string','N','2022-07-19 17:57:22'),(75,1,'smtp_username','','user name for smtp - send email','N','N','N','','string','N','2022-07-19 17:57:22'),(76,1,'smtp_password','','password for smtp - send email','N','Y','Y','','string','N','2022-07-19 17:57:22'),(77,1,'mailSender','','mail recepients details to send email','N','N','N','','string','N','2022-07-19 17:57:22'),(78,1,'mailRecepients','','mail sender details to send email','N','N','N','','string','N','2022-07-19 17:57:22'),(79,1,'mailxEmailNotification','N','Property to enable email notification via mailx','N','N','N','N','string','N','2022-07-19 17:57:22'),(80,1,'filemonitor.sns.aws.topicARN','','Property to specify the SNS Topic ARN for File monitor','N','N','N','','string','N','2022-07-19 17:57:22'),(81,1,'filemonitor.sns.aws.topic.region','','Property to specify the SNS Topic region for File monitor','N','N','N','','string','N','2022-07-19 17:57:22'),(82,1,'filemonitor.aws.accessKey','','aws secret key for File monitor SNS Topic','N','N','N','','string','N','2022-07-19 17:57:22'),(83,1,'filemonitor.aws.secretKey','','aws secret key for file monitor SNS Topic','N','Y','Y','','string','N','2022-07-19 17:57:22'),(84,3,'Context.INITIAL_CONTEXT_FACTORY','com.sun.jndi.ldap.LdapCtxFactory','initial context','Y','N','N','','string','Y','2022-07-19 17:57:22'),(85,3,'Context.PROVIDER_URL','ldap://<host>:<port>','provider url','Y','N','N','','string','Y','2022-07-19 17:57:22'),(86,3,'Context.SECURITY_AUTHENTICATION','simple','security authentication','Y','N','N','','string','Y','2022-07-19 17:57:22'),(87,3,'Context.SECURITY_PRINCIPAL','','user id','Y','N','N','','string','Y','2022-07-19 17:57:22'),(88,3,'Context.SECURITY_CREDENTIALS','','security credentials','Y','Y','Y','','string','Y','2022-07-19 17:57:22'),(89,3,'searchBase','','searchBase','Y','N','N','','string','Y','2022-07-19 17:57:22'),(90,3,'Domainforalluser','','domain name for all users','Y','N','N','','string','Y','2022-07-19 17:57:22'),(91,3,'Loginuid','','Loginuid','Y','N','N','','string','Y','2022-07-19 17:57:22'),(92,3,'userObjectClass','account','user object class','Y','N','N','','string','Y','2022-07-19 17:57:22'),(93,3,'attributetofetch','cn','attributetofetch','Y','N','N','','string','Y','2022-07-19 17:57:22'),(94,3,'domainPosition','3','domainPosition','Y','N','N','','string','Y','2022-07-19 17:57:22'),(95,3,'rolePosition','4','rolePosition','Y','N','N','','string','Y','2022-07-19 17:57:22'),(96,3,'groupFilter','1AOE','groupFilter','Y','N','N','','string','Y','2022-07-19 17:57:22'),(97,3,'defaultrole','','defaultrole of an user','N','N','N','','string','Y','2022-07-19 17:57:22'),(98,3,'sorPosition','','sorPosition','N','N','N','','string','Y','2022-07-19 17:57:22'),(99,4,'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y','2022-07-19 17:57:22'),(100,4,'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N','2022-07-19 17:57:22'),(101,4,'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N','2022-07-19 17:57:22'),(102,4,'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N','2022-07-19 17:57:22'),(103,4,'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N','2022-07-19 17:57:22'),(104,4,'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N','2022-07-19 17:57:22'),(105,4,'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N','2022-07-19 17:57:22'),(106,5,'LicenseKey','me4/fx+ns5DLew6tJKW3/ofBzud6B/adfGkEfrL8qwE=','Databuck License Key','Y','N','N','None','string','Y','2022-07-19 17:58:00'),(107,6,'ipAddress','','ipaddress to connect to mongodb','Y','N','N','','string','Y','2022-07-19 17:57:22'),(108,6,'port','','port details of mongodb','Y','N','N','','string','Y','2022-07-19 17:57:22'),(109,6,'databaseName','','mongodb database name to be connected','Y','N','N','','string','Y','2022-07-19 17:57:22'),(110,7,'DT','CN-idDataSchema','DataTemplate to Connection Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(111,7,'VC','DT-idData','Validation to DataTemplate Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(112,7,'LDS','CN-idDataSchema','ListDataSource to Connection Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(113,7,'LDA','CN-idDataSchema','ListDataApplication to Connection Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(114,7,'LDD','CN-idDataSchema','ListDataDefinition to Connection Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(115,7,'CN','','Connection Hierarchy','N','N','N','','string','N','2022-07-19 17:57:22'),(116,8,'db.driver','','driver details to connect to postgressql','Y','N','N','','string','N','2022-07-19 17:57:22'),(117,8,'db.url','','url to connect to postgressql','Y','N','N','','string','N','2022-07-19 17:57:22'),(118,8,'db.user','','user details to connect to postgressql','Y','N','N','','string','N','2022-07-19 17:57:22'),(119,8,'db.pwd','','pwd to connect to postgressql','Y','Y','Y','','string','N','2022-07-19 17:57:22'),(120,8,'target.table.dynamicFilterQuery','','','N','N','N','','string','N','2022-07-19 17:57:22'),(121,8,'src.table.dynamicFilterQuery','','','N','N','N','','string','N','2022-07-19 17:57:22'),(122,1,'consolidated.status.check.enabled','N','To display root cause analysis and exceptions enter \"Y\", else \"N\"','N','N','N','N','string','N','2022-07-19 17:57:22'),(123,1,'isDateRange_DownloadCsv_Enhancements','N','Temporary flag until all download CSV new enhancements are completed','N','N','N','N','string','N','2022-07-19 17:57:23'),(124,1,'unique.allowed.string.length','20','Property to edit the string length allowed','N','N','N','20','string','N','2022-07-19 17:57:23'),(125,1,'unique.count.allowed.percentage','99.5','Property to edit the string length allowed','N','N','N','99.5','string','N','2022-07-19 17:57:23'),(126,1,'unique.partitionkey.fieldname','eapp_dct_business_effective_date','Column name of unique partition key','N','N','N','','string','N','2022-07-19 17:57:23'),(127,1,'download.failed.data.reports.allowed','Y','To allow failed data reports download enter \"Y\", else \"N\"','N','N','N','Y','string','N','2022-07-19 17:57:23'),(128,1,'connection.healthcheck.enabled','N','Property to perform health check of connection','N','N','N','N','string','N','2022-07-19 17:57:23'),(129,9,'jira.integration.enabled','N','Property to enable health Jira Integration','N','N','N','N','string','N','2022-07-19 17:57:23'),(130,9,'jira.api.hostport','','Property to enter Jira host:port details','N','N','N','','string','N','2022-07-19 17:57:23'),(131,9,'jira.api.username','','Property to enter Jira username','N','N','N','','string','N','2022-07-19 17:57:23'),(132,9,'jira.api.apitoken','','Property to enter Jira APIToken','N','Y','Y','','string','N','2022-07-19 17:57:23'),(133,9,'jira.api.projectkey','','Property to enter ProjectKey','N','N','N','','string','N','2022-07-19 17:57:23'),(134,9,'jira.api.app.min.dqi','100.0','Property to enter min acceptable DQI, below which it is failure','N','N','N','100.0','double','N','2022-07-19 17:57:23'),(135,9,'alation.integration.enabled','N','Property to enable Alation Integration','N','N','N','N','string','N','2022-07-19 17:57:23'),(136,9,'alation.integration.baseurl','','Property to enter Alation BaseUrl details','N','N','N','','string','N','2022-07-19 17:57:23'),(137,9,'alation.integration.accesstoken','','Property to enter Alation AccessToken','N','Y','Y','','string','N','2022-07-19 17:57:23'),(138,9,'alation.integration.refreshtoken','','Property to enter Alation RefreshToken','N','Y','Y','','string','N','2022-07-19 17:57:23'),(139,9,'databuck.baseurl','','Property to enter Databuck UI base url','N','N','N','localhost:8080','string','N','2022-07-19 17:57:23'),(140,1,'snowflake.timezone','','Property to set the snowflake timezone','N','N','N','','string','N','2022-07-19 17:57:24'),(141,1,'default.pattern.threshold','10','Min frequency of occurrence of a pattern to be recognized as an acceptable pattern (in %). Eg., 7','N','N','N','','string','N','2022-07-19 17:57:24'),(142,1,'default.pattern.top3.threshold','90','Threshold for Top three default patterns','N','N','N','','string','N','2022-07-19 17:57:24'),(143,1,'incremental.lastread.column.name','','Date and/or time column used to identify the last read-time','N','N','N','','string','N','2022-07-19 17:57:24'),(144,1,'auto.incremental.validation.creation.enabled','N','For automatic incremental validation creation, enter \"Y\" else \"N\"','N','N','N','N','string','N','2022-07-19 17:57:24'),(145,1,'azure_result_directory','','Directory where Azure results are stored','N','N','N','','string','N','2022-07-19 17:57:24'),(146,9,'alation.integration.dqi.threshold','','Property to enter threshold DQI, below which it is considered failure','N','N','N','90','string','N','2022-07-19 17:57:24'),(147,1,'completeness.exclusion.special.columns','','Column names in which specified keywords should be considered as Nulls, eg., Col-A, Col-B, ….','N','N','N','','string','N','2022-07-19 17:57:25'),(148,1,'completeness.special.columns.exclusion.keywords','','These keywords should be considered as Nulls for the specified columns, eg., Blank, N/A,…','N','N','N','','string','N','2022-07-19 17:57:25'),(149,1,'completeness.exclusion.keywords','','These keywords should be considered as Nulls for all Null check enabled columns, eg., Blank, N/A,…','N','N','N','','string','N','2022-07-19 17:57:25'),(150,1,'hostNamesForCORS','http://localhost:4200','Host for CORS policy','N','N','N','','string','Y','2022-07-19 17:57:25'),(151,1,'enable.autosynonym.detection','N','Property to enable auto detection of synonyms when new template is created','N','N','N','N','string','N','2022-07-19 17:57:25'),(152,1,'detailed.profiling.enabled','N','Property to enable detailed profiling of template','N','N','N','N','string','N','2022-07-19 17:57:25'),(153,4,'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y','2022-07-19 17:57:26'),(154,4,'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y','2022-07-19 17:57:26'),(155,4,'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y','2022-07-19 17:57:26'),(156,4,'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N','2022-07-19 17:57:26'),(157,4,'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:26'),(158,10,'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y','2022-07-19 17:57:26'),(159,10,'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N','2022-07-19 17:57:26'),(160,10,'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N','2022-07-19 17:57:26'),(161,10,'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N','2022-07-19 17:57:26'),(162,10,'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N','2022-07-19 17:57:26'),(163,10,'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N','2022-07-19 17:57:26'),(164,10,'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N','2022-07-19 17:57:26'),(165,10,'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y','2022-07-19 17:57:26'),(166,10,'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y','2022-07-19 17:57:26'),(167,10,'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y','2022-07-19 17:57:26'),(168,10,'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N','2022-07-19 17:57:26'),(169,10,'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:26'),(170,11,'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y','2022-07-19 17:57:26'),(171,11,'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N','2022-07-19 17:57:26'),(172,11,'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N','2022-07-19 17:57:26'),(173,11,'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N','2022-07-19 17:57:26'),(174,11,'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N','2022-07-19 17:57:26'),(175,11,'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N','2022-07-19 17:57:26'),(176,11,'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N','2022-07-19 17:57:26'),(177,11,'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y','2022-07-19 17:57:26'),(178,11,'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y','2022-07-19 17:57:26'),(179,11,'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y','2022-07-19 17:57:26'),(180,11,'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N','2022-07-19 17:57:26'),(181,11,'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:26'),(182,12,'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y','2022-07-19 17:57:26'),(183,12,'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N','2022-07-19 17:57:26'),(184,12,'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N','2022-07-19 17:57:26'),(185,12,'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N','2022-07-19 17:57:26'),(186,12,'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N','2022-07-19 17:57:26'),(187,12,'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N','2022-07-19 17:57:26'),(188,12,'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N','2022-07-19 17:57:26'),(189,12,'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y','2022-07-19 17:57:26'),(190,12,'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y','2022-07-19 17:57:26'),(191,12,'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y','2022-07-19 17:57:26'),(192,12,'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N','2022-07-19 17:57:26'),(193,12,'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:26'),(194,13,'deploymode','2','spark deploy mode local mode value is 2, cluster mode value is 1','Y','N','N','2','string','Y','2022-07-19 17:57:26'),(195,13,'hive_mode','remote','Property to enable/ disable hive context. deploymode =1 i.e, cluster mode and hive_mode value is cluster starts sparksession with hivesupport enabled.','Y','N','N','','string','N','2022-07-19 17:57:26'),(196,13,'numberOfPartitions','100','Property to specify number of partitions to configure in spark job','Y','N','N','','string','N','2022-07-19 17:57:26'),(197,13,'kerberos_enabled','N','Property to enable or diable kerberos','Y','N','N','','string','N','2022-07-19 17:57:26'),(198,13,'app_mode','0','app_mode value 0 indicates local and value 1 indicates mongo db','Y','N','N','','string','N','2022-07-19 17:57:26'),(199,13,'EMRCluster','N','Property to enable/disable connect to EMR cluster','N','N','N','N','string','N','2022-07-19 17:57:26'),(200,13,'hive_context_enabled','N','Property to enable or disable data read via Hive context','N','N','N','N','string','N','2022-07-19 17:57:26'),(201,13,'deploymentMode','local','Property to mention in which mode application is running local, hdfs or s3','Y','N','N','local','string','Y','2022-07-19 17:57:26'),(202,13,'mapr.ticket.enabled','N','Property to enable or disable usage of ticket during script trigger','Y','N','N','N','string','Y','2022-07-19 17:57:26'),(203,13,'hdfs_result_directory','','Property to mention the hdfs directory where result files will be stored, when deployment mode is hdfs','N','N','N','','string','Y','2022-07-19 17:57:26'),(204,13,'hdfs.accesslog.path','N','Access log folder path in hdfs','N','N','N','','string','N','2022-07-19 17:57:26'),(205,13,'hdfs_uri','','Property to specify the hdfs URI to connect to hdfs filesystem to download result files','N','N','N','','string','Y','2022-07-19 17:57:26'),(206,1,'default.pattern.skip.numericfields','N','Skip Numeric fields in default Pattern Check','N','N','N','','string','N','2022-07-19 17:57:26'),(207,1,'default.pattern.numthread','1','Number of threads for default patterns','N','N','N','','string','N','2022-07-19 17:57:26'),(208,1,'composite.primarykey.disable.flag','N','Disable identification of Composite key','N','N','N','','string','N','2022-07-19 17:57:26'),(209,1,'databuck.module','all','Property to mention the name of module','N','N','N','all','string','N','2022-07-19 17:57:26');
/*!40000 ALTER TABLE databuck_property_details ENABLE KEYS */;

--
-- Table structure for table databuck_security_matrix
--

DROP TABLE IF EXISTS databuck_security_matrix;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE databuck_security_matrix_seq;

CREATE TABLE databuck_security_matrix (
  row_id int NOT NULL DEFAULT NEXTVAL ('databuck_security_matrix_seq'),
  ldap_group_name varchar(255) DEFAULT NULL,
  idRole int DEFAULT NULL,
  idSORs int NOT NULL DEFAULT '0',
  domainId int NOT NULL DEFAULT '0',
  idProject int DEFAULT NULL,
  idUser int DEFAULT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT con_databuck_security_matrix UNIQUE (ldap_group_name,idRole,idProject,idSORs)
) ;

--
-- Table structure for table dataset_definition
--

DROP TABLE IF EXISTS dataset_definition;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dataset_definition_seq;

CREATE TABLE dataset_definition (
  id int NOT NULL DEFAULT NEXTVAL ('dataset_definition_seq'),
  column_name varchar(255) NOT NULL,
  display_name varchar(255) NOT NULL,
  format varchar(255) NOT NULL,
  seq int NOT NULL,
  datasource_id int NOT NULL,
  PRIMARY KEY (id)
 ,
  CONSTRAINT dataset_definition_ibfk_1 FOREIGN KEY (datasource_id) REFERENCES data_source (id)
)  ;

ALTER SEQUENCE dataset_definition_seq RESTART WITH 7562;

CREATE INDEX index_datasource_id ON dataset_definition (datasource_id);

--
-- Table structure for table dbk_file_monitor_rules
--

DROP TABLE IF EXISTS dbk_file_monitor_rules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dbk_file_monitor_rules_seq;

CREATE TABLE dbk_file_monitor_rules (
  Id int NOT NULL DEFAULT NEXTVAL ('dbk_file_monitor_rules_seq'),
  connection_id int NOT NULL,
  validation_id int NOT NULL,
  schema_name varchar(250) NOT NULL,
  table_name varchar(1000) NOT NULL,
  file_indicator varchar(50) NOT NULL,
  dayOfWeek varchar(50) NOT NULL,
  hourOfDay smallint DEFAULT NULL,
  expected_time smallint DEFAULT NULL,
  expected_file_count smallint DEFAULT NULL,
  start_hour smallint DEFAULT NULL,
  end_hour smallint DEFAULT NULL,
  frequency smallint DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE dbk_file_monitor_rules_seq RESTART WITH 100;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table defect_codes
--

DROP TABLE IF EXISTS defect_codes;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE defect_codes_seq;

CREATE TABLE defect_codes (
  row_id int NOT NULL DEFAULT NEXTVAL ('defect_codes_seq'),
  defect_code varchar(50) DEFAULT NULL,
  defect_description varchar(255) DEFAULT NULL,
  dimension_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_defect_code UNIQUE (defect_code),
  CONSTRAINT unique_defect_dimension UNIQUE (defect_code,dimension_id)
) ;

--
-- Table structure for table dimension
--

DROP TABLE IF EXISTS dimension;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE dimension_seq;

CREATE TABLE dimension (
  idDimension int NOT NULL DEFAULT NEXTVAL ('dimension_seq'),
  dimensionName varchar(45) NOT NULL,
  PRIMARY KEY (idDimension),
  CONSTRAINT con_dimension UNIQUE (idDimension,dimensionName)
)  ;

ALTER SEQUENCE dimension_seq RESTART WITH 7;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table dimension
--

/*!40000 ALTER TABLE dimension DISABLE KEYS */;
INSERT INTO dimension VALUES (1,'undefined'),(2,'Validity'),(3,'Completeness'),(4,'Consistency'),(5,'Accuracy'),(6,'Uniqueness');
/*!40000 ALTER TABLE dimension ENABLE KEYS */;

--
-- Table structure for table domain
--

DROP TABLE IF EXISTS domain;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE domain_seq;

CREATE TABLE domain (
  domainId int NOT NULL DEFAULT NEXTVAL ('domain_seq'),
  domainName varchar(45) DEFAULT NULL,
  is_enterprise_domain smallint DEFAULT 0,
  description varchar(255) DEFAULT NULL,
  PRIMARY KEY (domainId)
)  ;

ALTER SEQUENCE domain_seq RESTART WITH 7;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table domain
--

/*!40000 ALTER TABLE domain DISABLE KEYS */;
INSERT INTO domain VALUES (1,'Banking', 0,NULL),(2,'Telecom', 0,NULL),(3,'Finance', 0,NULL),(4,'Medical', 0,NULL),(5,'Advertisement', 0,NULL),(6,'Others', 0,NULL);
/*!40000 ALTER TABLE domain ENABLE KEYS */;

--
-- Table structure for table domain_jobs_queue
--

DROP TABLE IF EXISTS domain_jobs_queue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE domain_jobs_queue_seq;

CREATE TABLE domain_jobs_queue (
  queueId int NOT NULL DEFAULT NEXTVAL ('domain_jobs_queue_seq'),
  domainId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  processId int DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  PRIMARY KEY (queueId)
)  ;

ALTER SEQUENCE domain_jobs_queue_seq RESTART WITH 100;

--
-- Table structure for table domain_jobs_tracking
--

DROP TABLE IF EXISTS domain_jobs_tracking;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE domain_jobs_tracking_seq;

CREATE TABLE domain_jobs_tracking (
  id int NOT NULL DEFAULT NEXTVAL ('domain_jobs_tracking_seq'),
  domainId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  projectId int NOT NULL,
  project_uniqueId varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE domain_jobs_tracking_seq RESTART WITH 100;

--
-- Table structure for table domain_lite_jobs_queue
--

DROP TABLE IF EXISTS domain_lite_jobs_queue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE domain_lite_jobs_queue_seq;

CREATE TABLE domain_lite_jobs_queue (
  queueId int NOT NULL DEFAULT NEXTVAL ('domain_lite_jobs_queue_seq'),
  domainId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  resultJson text,
  PRIMARY KEY (queueId)
)  ;

ALTER SEQUENCE domain_lite_jobs_queue_seq RESTART WITH 100;

--
-- Table structure for table domain_to_project
--

DROP TABLE IF EXISTS domain_to_project;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE domain_to_project_seq;

CREATE TABLE domain_to_project (
  row_id int NOT NULL DEFAULT NEXTVAL ('domain_to_project_seq'),
  domain_id int NOT NULL,
  project_id int NOT NULL,
  is_owner varchar(1) NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT con_domain_to_project UNIQUE (domain_id,project_id)
) ;

--
-- Table structure for table dtbkdbsess
--

DROP TABLE IF EXISTS dtbkdbsess;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE dtbkdbsess (
  id varchar(40) NOT NULL,
  ip_address varchar(45) NOT NULL,
  timestamp int NOT NULL,
  data bytea NOT NULL
) ;

--
-- Table structure for table exception_data_report
--

DROP TABLE IF EXISTS exception_data_report;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE exception_data_report_seq;

CREATE TABLE exception_data_report (
  row_id int NOT NULL DEFAULT NEXTVAL ('exception_data_report_seq'),
  report_id int NOT NULL,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  data_frequency smallint DEFAULT NULL,
  project_id int NOT NULL,
  created_by int NOT NULL,
  created_date timestamp(0) NOT NULL,
  modified_by int NOT NULL,
  modified_date timestamp(0) NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_report_name UNIQUE (project_id,name)
) ;

--
-- Table structure for table exception_data_report_apps
--

DROP TABLE IF EXISTS exception_data_report_apps;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE exception_data_report_apps_seq;

CREATE TABLE exception_data_report_apps (
  row_id int NOT NULL DEFAULT NEXTVAL ('exception_data_report_apps_seq'),
  report_row_id int NOT NULL,
  app_row_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_report_app UNIQUE (report_row_id,app_row_id)
) ;

--
-- Table structure for table file_monitor_rules
--

DROP TABLE IF EXISTS file_monitor_rules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE file_monitor_rules_seq;

CREATE TABLE file_monitor_rules (
  id int NOT NULL DEFAULT NEXTVAL ('file_monitor_rules_seq'),
  bucketName varchar(255) DEFAULT NULL,
  dayOfCheck int DEFAULT NULL,
  fileCount int DEFAULT NULL,
  filePattern varchar(255) DEFAULT NULL,
  fileSizeThreshold int DEFAULT NULL,
  folderPath varchar(255) DEFAULT NULL,
  frequency varchar(255) DEFAULT NULL,
  idApp int NOT NULL,
  lastProcessedDate timestamp(0) DEFAULT NULL,
  timeOfCheck varchar(255) DEFAULT NULL,
  idDataSchema int DEFAULT NULL,
  partitionedFolders varchar(10) NOT NULL DEFAULT 'N',
  PRIMARY KEY (id)
) ;

--
-- Table structure for table file_tracking_history
--

DROP TABLE IF EXISTS file_tracking_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE file_tracking_history_seq;

CREATE TABLE file_tracking_history (
  id int NOT NULL DEFAULT NEXTVAL ('file_tracking_history_seq'),
  bucketName varchar(255) DEFAULT NULL,
  date date DEFAULT NULL,
  dayOfMonth int NOT NULL,
  dayOfWeek int DEFAULT NULL,
  dayOfYear int NOT NULL,
  fileArrivalDate timestamp(0) DEFAULT NULL,
  fileArrivalTime varchar(255) DEFAULT NULL,
  fileMonitorRuleId int NOT NULL,
  fileName varchar(255) DEFAULT NULL,
  fileSize int NOT NULL,
  folderPath varchar(255) DEFAULT NULL,
  hourOfDay varchar(255) DEFAULT NULL,
  idApp int NOT NULL,
  month varchar(255) DEFAULT NULL,
  requestId varchar(255) DEFAULT NULL,
  run int NOT NULL,
  status varchar(255) DEFAULT NULL,
  statusMessage varchar(255) DEFAULT NULL,
  connectionName varchar(255) DEFAULT NULL,
  trackingDate timestamp(0) DEFAULT NULL,
  idData int DEFAULT NULL,
  fileExecutionStatus varchar(20) NOT NULL DEFAULT 'unprocessed',
  fileExecutionStatusMsg varchar(2000) DEFAULT NULL,
  columnCountCheck varchar(255) DEFAULT 'null',
  columnSequenceCheck varchar(255) DEFAULT 'null',
  fileFormat varchar(255) DEFAULT 'null',
  recordLengthCheck varchar(255) DEFAULT 'null',
  zeroSizeFileCheck varchar(255) DEFAULT 'null',
  recordMaxLengthCheck varchar(255) DEFAULT 'null',
  PRIMARY KEY (id)
) ;

--
-- Table structure for table file_tracking_summary
--

DROP TABLE IF EXISTS file_tracking_summary;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE file_tracking_summary_seq;

CREATE TABLE file_tracking_summary (
  id int NOT NULL DEFAULT NEXTVAL ('file_tracking_summary_seq'),
  countStatus varchar(255) DEFAULT NULL,
  date date DEFAULT NULL,
  dayOfMonth int NOT NULL,
  dayOfWeek int DEFAULT NULL,
  dayOfYear int NOT NULL,
  fileCount int DEFAULT NULL,
  fileSizeStatus varchar(255) DEFAULT NULL,
  hourOfDay varchar(255) DEFAULT NULL,
  idApp int NOT NULL,
  lastUpdateTimeStamp timestamp(0) DEFAULT NULL,
  month varchar(255) DEFAULT NULL,
  run int NOT NULL,
  fileMonitorRules_id int DEFAULT NULL,
  trackingDate timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

CREATE INDEX FK_8hgxxxvqb3ssk22j151jkbrck ON file_tracking_summary (fileMonitorRules_id);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table fm_connection_details
--

DROP TABLE IF EXISTS fm_connection_details;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE fm_connection_details_seq;

CREATE TABLE fm_connection_details (
  Id int NOT NULL DEFAULT NEXTVAL ('fm_connection_details_seq'),
  idApp int NOT NULL,
  idDataSchema int NOT NULL,
  last_load_time timestamp(0) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE fm_connection_details_seq RESTART WITH 100;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table global_filters
--

DROP TABLE IF EXISTS global_filters;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE global_filters_seq;

CREATE TABLE global_filters (
  global_filter_id int NOT NULL DEFAULT NEXTVAL ('global_filters_seq'),
  global_filter_name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  global_filter_condition text,
  createdAt timestamp(0) DEFAULT NULL,
  domain_id int DEFAULT NULL,
  PRIMARY KEY (global_filter_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table projgroup
--

DROP TABLE IF EXISTS projgroup;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE projgroup_seq;

CREATE TABLE projgroup (
  idGroup int NOT NULL DEFAULT NEXTVAL ('projgroup_seq'),
  groupName varchar(100) DEFAULT NULL,
  description varchar(100) DEFAULT NULL,
  createdAt date DEFAULT NULL,
  updatedAt date DEFAULT NULL,
  PRIMARY KEY (idGroup)
) ;

--
-- Table structure for table grouptouser
--

DROP TABLE IF EXISTS grouptouser;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE grouptouser (
  idGroup int DEFAULT NULL,
  idUser int DEFAULT NULL,
  CONSTRAINT idGrouptoGroup FOREIGN KEY (idGroup) REFERENCES projgroup (idGroup) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT idUsertoUser FOREIGN KEY (idUser) REFERENCES "User" (idUser) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

CREATE INDEX idGrouptoGroup_idx ON grouptouser (idGroup);
CREATE INDEX idUsertoUser_idx ON grouptouser (idUser);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table hiveSource
--

DROP TABLE IF EXISTS hiveSource;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE hiveSource_seq;

CREATE TABLE hiveSource (
  idHiveSource int NOT NULL DEFAULT NEXTVAL ('hiveSource_seq'),
  name varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  idDataSchema int NOT NULL,
  tableName text NOT NULL,
  columnName text,
  columnType text,
  recordCount int DEFAULT NULL,
  totalTables int DEFAULT NULL,
  completedTables int DEFAULT NULL,
  PRIMARY KEY (idHiveSource)
) ;

--
-- Table structure for table inProcessFiles
--

DROP TABLE IF EXISTS inProcessFiles;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE inProcessFiles (
  fileCompletePath varchar(1000) DEFAULT NULL,
  status varchar(50) DEFAULT NULL
) ;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table listAdvancedRules
--

DROP TABLE IF EXISTS listAdvancedRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listAdvancedRules_seq;

CREATE TABLE listAdvancedRules (
  ruleId int NOT NULL DEFAULT NEXTVAL ('listAdvancedRules_seq'),
  idData int DEFAULT NULL,
  ruleType varchar(500) DEFAULT NULL,
  columnName varchar(500) DEFAULT NULL,
  ruleExpr text,
  ruleSql text,
  idListColrules int DEFAULT NULL,
  isCustomRuleEligible varchar(10) DEFAULT NULL,
  isRuleActive varchar(10) DEFAULT 'N',
  Date date DEFAULT NULL,
  Run int DEFAULT NULL,
  PRIMARY KEY (ruleId)
)  ;

ALTER SEQUENCE listAdvancedRules_seq RESTART WITH 5937;

--
-- Table structure for table listAppGroup
--

DROP TABLE IF EXISTS listAppGroup;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listAppGroup_seq;

CREATE TABLE listAppGroup (
  idAppGroup int NOT NULL DEFAULT NEXTVAL ('listAppGroup_seq'),
  name varchar(45) NOT NULL,
  description varchar(200) DEFAULT '',
  project_id int DEFAULT NULL,
  enableScheduling varchar(10) DEFAULT 'N',
  idSchedule int DEFAULT NULL,
  PRIMARY KEY (idAppGroup)
) ;

--
-- Table structure for table listDataBlend
--

DROP TABLE IF EXISTS listDataBlend;

CREATE SEQUENCE listDataBlend_seq;

CREATE TABLE listDataBlend (
  idDataBlend int NOT NULL DEFAULT NEXTVAL ('listDataBlend_seq'),
  idData int NOT NULL,
  idColumn int DEFAULT NULL,
  name varchar(45) NOT NULL,
  description varchar(45) NOT NULL,
  expression varchar(500) DEFAULT NULL,
  columnName varchar(100) DEFAULT NULL,
  derivedColType varchar(50) DEFAULT NULL,
  columnValue varchar(200) DEFAULT NULL,
  columnValueType varchar(20) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  createdBy int DEFAULT NULL,
  updatedBy int DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  project_id int DEFAULT NULL,
  PRIMARY KEY (idDataBlend)
) ;

--
-- Table structure for table listDataSources
--

DROP TABLE IF EXISTS listDataSources;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataSources_seq;

CREATE TABLE listDataSources (
  idData int NOT NULL DEFAULT NEXTVAL ('listDataSources_seq'),
  name varchar(1000) NOT NULL DEFAULT '',
  description varchar(1000) NOT NULL,
  dataLocation varchar(45) NOT NULL,
  dataSource varchar(45) NOT NULL,
  createdBy int NOT NULL,
  idDataBlend int DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  updatedBy int DEFAULT NULL,
  schemaName varchar(500) DEFAULT NULL,
  idDataSchema int NOT NULL,
  ignoreRowsCount int NOT NULL,
  active varchar(10) DEFAULT 'yes',
  project_id int DEFAULT NULL,
  profilingEnabled varchar(20) DEFAULT NULL,
  advancedRulesEnabled varchar(20) DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  domain_id int DEFAULT NULL,
  template_create_success varchar(20) DEFAULT 'N',
  deltaApprovalStatus varchar(2000) DEFAULT NULL,
  subcribed_email_id varchar(1000) DEFAULT NULL,
  PRIMARY KEY (idData)
 ,
  CONSTRAINT listDataSources_ibfk_1 FOREIGN KEY (idDataBlend) REFERENCES listDataBlend (idDataBlend) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDataSources_seq RESTART WITH 288;

CREATE INDEX listDataSources_ibfk_1 ON listDataSources (idDataBlend);
CREATE INDEX dbPer_listDataSources ON listDataSources (project_id);

--
-- Table structure for table listApplications
--

DROP TABLE IF EXISTS listApplications;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listApplications_seq;

CREATE TABLE listApplications (
  idApp int NOT NULL DEFAULT NEXTVAL ('listApplications_seq'),
  name text NOT NULL,
  description text,
  appType varchar(45) NOT NULL,
  idData int NOT NULL,
  idRightData int DEFAULT NULL,
  createdBy int NOT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  updatedBy int DEFAULT NULL,
  fileNameValidation varchar(20) NOT NULL,
  entityColumn varchar(100) NOT NULL,
  colOrderValidation varchar(20) NOT NULL,
  matchingThreshold double precision DEFAULT '0',
  nonNullCheck varchar(45) DEFAULT NULL,
  numericalStatCheck varchar(10) DEFAULT NULL,
  stringStatCheck varchar(10) DEFAULT NULL,
  recordAnomalyCheck varchar(10) DEFAULT NULL,
  incrementalMatching varchar(10) NOT NULL DEFAULT 'N',
  incrementalTimestamp timestamp(0) DEFAULT NULL,
  dataDriftCheck varchar(10) NOT NULL DEFAULT 'N',
  updateFrequency varchar(100) NOT NULL DEFAULT 'Never',
  frequencyDays int DEFAULT NULL,
  recordCountAnomaly varchar(10) NOT NULL DEFAULT 'N',
  recordCountAnomalyThreshold double precision NOT NULL DEFAULT '0',
  timeSeries varchar(500) NOT NULL DEFAULT 'None',
  keyGroupRecordCountAnomaly varchar(500) DEFAULT NULL,
  outOfNormCheck varchar(50) DEFAULT 'N',
  applyRules varchar(10) NOT NULL DEFAULT 'N',
  applyDerivedColumns varchar(10) NOT NULL DEFAULT 'N',
  csvDir varchar(500) DEFAULT NULL,
  groupEquality varchar(20) NOT NULL DEFAULT 'N',
  groupEqualityThreshold double precision DEFAULT '0',
  buildHistoricFingerPrint varchar(20) DEFAULT 'N',
  historicStartDate timestamp(0) DEFAULT NULL,
  historicEndDate timestamp(0) DEFAULT NULL,
  historicDateFormat varchar(200) DEFAULT NULL,
  active varchar(10) DEFAULT 'yes',
  lengthCheck varchar(45) DEFAULT NULL,
  correlationcheck varchar(10) DEFAULT 'N',
  project_id int DEFAULT NULL,
  timelinessKeyCheck varchar(5) DEFAULT NULL,
  defaultCheck varchar(12) DEFAULT NULL,
  defaultValues varchar(12) DEFAULT NULL,
  patternCheck varchar(10) DEFAULT 'N',
  dateRuleCheck varchar(5) DEFAULT NULL,
  badData varchar(5) DEFAULT NULL,
  idLeftData int DEFAULT NULL,
  prefix1 varchar(100) DEFAULT NULL,
  prefix2 varchar(100) DEFAULT NULL,
  dGroupNullCheck varchar(10) DEFAULT NULL,
  dGroupDateRuleCheck varchar(100) DEFAULT NULL,
  fuzzylogic varchar(5) DEFAULT NULL,
  fileMonitoringType varchar(200) DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  validityThreshold double precision DEFAULT NULL,
  dGroupDataDriftCheck varchar(10) DEFAULT NULL,
  rollTargetSchemaId int DEFAULT NULL,
  thresholdsApplyOption int NOT NULL DEFAULT '0',
  continuousFileMonitoring varchar(10) DEFAULT 'N',
  rollType varchar(50) DEFAULT NULL,
  approve_status int DEFAULT NULL,
  approve_comments varchar(2000) DEFAULT NULL,
  approve_date timestamp(0) DEFAULT NULL,
  approve_by int DEFAULT NULL,
  domain_id int NOT NULL DEFAULT '0',
  subcribed_email_id varchar(1000) DEFAULT NULL,
  approver_name varchar(2500) DEFAULT NULL,
  data_domain_id smallint DEFAULT NULL,
  staging_approve_status int DEFAULT NULL,
  maxLengthCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatternCheck varchar(10) NOT NULL DEFAULT 'N',
  PRIMARY KEY (idApp),
  CONSTRAINT lapp_ibfk_1 FOREIGN KEY (idData) REFERENCES listDataSources (idData) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listApplications_seq RESTART WITH 360;

CREATE INDEX lapp_ibfk_1 ON listApplications (idData);
CREATE INDEX dbPer_listApplications ON listApplications (project_id,idApp);

--
-- Table structure for table listAppOwner
--

DROP TABLE IF EXISTS listAppOwner;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listAppOwner_seq;

CREATE TABLE listAppOwner (
  idlistAppOwner int NOT NULL DEFAULT NEXTVAL ('listAppOwner_seq'),
  idApp int NOT NULL,
  idGroup int NOT NULL,
  PRIMARY KEY (idlistAppOwner)
 ,
  CONSTRAINT lapowner_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX lapowner_ibfk_1 ON listAppOwner (idApp);

--
-- Table structure for table listSchedule
--

DROP TABLE IF EXISTS listSchedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listSchedule_seq;

CREATE TABLE listSchedule (
  idSchedule int NOT NULL DEFAULT NEXTVAL ('listSchedule_seq'),
  time time(0) DEFAULT NULL,
  name varchar(45) NOT NULL,
  description varchar(200) DEFAULT '',
  frequency varchar(45) NOT NULL,
  scheduleDay varchar(255) DEFAULT NULL,
  exceptionMatching varchar(10) NOT NULL DEFAULT 'N',
  project_id int DEFAULT NULL,
  PRIMARY KEY (idSchedule)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table listAppSchedule
--

DROP TABLE IF EXISTS listAppSchedule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listAppSchedule_seq;

CREATE TABLE listAppSchedule (
  idlistAppSchedule int NOT NULL DEFAULT NEXTVAL ('listAppSchedule_seq'),
  idSchedule int NOT NULL,
  idApp int NOT NULL,
  listAppSchedulecol varchar(45) DEFAULT NULL,
  PRIMARY KEY (idlistAppSchedule)
 ,
  CONSTRAINT listAppSchedule_ibfk_1 FOREIGN KEY (idSchedule) REFERENCES listSchedule (idSchedule) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT listAppSchedule_ibfk_2 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listAppSchedule_ibfk_1 ON listAppSchedule (idSchedule);
CREATE INDEX listAppSchedule_ibfk_2 ON listAppSchedule (idApp);



--
-- Table structure for table listApplicationsRulesCatalog
--

DROP TABLE IF EXISTS listApplicationsRulesCatalog;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listApplicationsRulesCatalog_seq;

CREATE TABLE listApplicationsRulesCatalog (
  row_id int NOT NULL DEFAULT NEXTVAL ('listApplicationsRulesCatalog_seq'),
  idApp int DEFAULT NULL,
  rule_reference int DEFAULT NULL,
  rule_code varchar(255) DEFAULT NULL,
  defect_code varchar(255) DEFAULT NULL,
  rule_type varchar(255) NOT NULL,
  column_name varchar(255) NOT NULL,
  rule_category varchar(255) NOT NULL,
  rule_expression text,
  threshold_value double precision NOT NULL DEFAULT '0',
  review_comments varchar(2000) DEFAULT NULL,
  review_date timestamp(0) DEFAULT NULL,
  review_by varchar(255) DEFAULT NULL,
  rule_name text,
  activeFlag smallint NOT NULL,
  dimension_id int DEFAULT NULL,
  agingCheckEnabled varchar(100) DEFAULT 'N',
  matching_rules text,
  custom_or_global_ruleId int DEFAULT NULL,
  rule_description text,
  custom_or_global_rule_type varchar(2000) DEFAULT NULL,
  filter_condition text,
  PRIMARY KEY (row_id)
) ;

--
-- Table structure for table listDAStandardRules
--

DROP TABLE IF EXISTS listDAStandardRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDAStandardRules_seq;

CREATE TABLE listDAStandardRules (
  idDA int NOT NULL DEFAULT NEXTVAL ('listDAStandardRules_seq'),
  idApp int NOT NULL,
  bendford varchar(1) DEFAULT 'N',
  outlier varchar(1) DEFAULT 'N',
  custom varchar(1) DEFAULT 'N',
  PRIMARY KEY (idDA)
 ,
  CONSTRAINT ldasr_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX ldasr_ibfk_1 ON listDAStandardRules (idApp);

--
-- Table structure for table listBenfordRules
--

DROP TABLE IF EXISTS listBenfordRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listBenfordRules_seq;

CREATE TABLE listBenfordRules (
  idlistBenfordRules int NOT NULL DEFAULT NEXTVAL ('listBenfordRules_seq'),
  idDA int NOT NULL,
  idCol int NOT NULL,
  PRIMARY KEY (idlistBenfordRules)
 ,
  CONSTRAINT listBenfordR_ibfk_1 FOREIGN KEY (idDA) REFERENCES listDAStandardRules (idDA) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listBenfordR_ibfk_1 ON listBenfordRules (idDA);

--
-- Table structure for table project
--

DROP TABLE IF EXISTS project;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE project_seq;

CREATE TABLE project (
  idProject int NOT NULL DEFAULT NEXTVAL ('project_seq'),
  projectName varchar(100) DEFAULT NULL,
  projectDescription varchar(100) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  notification_email varchar(255) DEFAULT NULL,
  PRIMARY KEY (idProject)
)  ;

ALTER SEQUENCE project_seq RESTART WITH 17;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table listColGlobalRules
--

DROP TABLE IF EXISTS listColGlobalRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listColGlobalRules_seq;

CREATE TABLE listColGlobalRules (
  idListColrules int NOT NULL DEFAULT NEXTVAL ('listColGlobalRules_seq'),
  ruleName varchar(2500) DEFAULT NULL,
  description varchar(2500) DEFAULT NULL,
  createdAt date DEFAULT NULL,
  expression text,
  domain_id int DEFAULT NULL,
  project_id int DEFAULT NULL,
  ruleType varchar(45) DEFAULT NULL,
  externalDatasetName varchar(245) DEFAULT NULL,
  idRightData int DEFAULT NULL,
  matchingRules text,
  createdByUser varchar(1000) DEFAULT NULL,
  ruleThreshold double precision DEFAULT '0',
  dimension_id int NOT NULL,
  filterId int DEFAULT NULL,
  aggregateResultsEnabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (idListColrules)
 ,
  CONSTRAINT lcolrule_proj_id4 FOREIGN KEY (project_id) REFERENCES project (idProject) ON DELETE NO ACTION ON UPDATE NO ACTION
)  ;

ALTER SEQUENCE listColGlobalRules_seq RESTART WITH 18;

CREATE INDEX lcolrule_proj_id_idx ON listColGlobalRules (project_id);

--
-- Table structure for table listColRules
--

DROP TABLE IF EXISTS listColRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listColRules_seq;

CREATE TABLE listColRules (
  idListColrules int NOT NULL DEFAULT NEXTVAL ('listColRules_seq'),
  idData int DEFAULT NULL,
  idCol int DEFAULT NULL,
  ruleName text,
  description varchar(2500) DEFAULT NULL,
  createdAt date DEFAULT NULL,
  ruleType varchar(45) DEFAULT NULL,
  expression text,
  external varchar(245) DEFAULT NULL,
  externalDatasetName varchar(245) DEFAULT NULL,
  idRightData int DEFAULT NULL,
  matchingRules text,
  matchType varchar(100) DEFAULT NULL,
  sourcetemplateone varchar(45) DEFAULT NULL,
  sourcetemplatesecond varchar(45) DEFAULT NULL,
  ruleThreshold double precision DEFAULT '0',
  createdByUser varchar(1000) DEFAULT NULL,
  project_id int DEFAULT NULL,
  domain_id int NOT NULL DEFAULT '0',
  domensionId int NOT NULL DEFAULT '1',
  activeFlag varchar(10) DEFAULT 'Y',
  anchorColumns text,
  PRIMARY KEY (idListColrules)
)  ;

ALTER SEQUENCE listColRules_seq RESTART WITH 61;

--
-- Table structure for table listCustomRules
--

DROP TABLE IF EXISTS listCustomRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listCustomRules_seq;

CREATE TABLE listCustomRules (
  idlistCustomRules int NOT NULL DEFAULT NEXTVAL ('listCustomRules_seq'),
  idDA int NOT NULL,
  leftExp varchar(200) NOT NULL,
  doOperator varchar(45) NOT NULL,
  rightExp varchar(200) NOT NULL,
  PRIMARY KEY (idlistCustomRules)
 ,
  CONSTRAINT listCustomRule_ibfk_1 FOREIGN KEY (idDA) REFERENCES listDAStandardRules (idDA) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listCustomRule_ibfk_1 ON listCustomRules (idDA);

--
-- Table structure for table listDFColRule
--

DROP TABLE IF EXISTS listDFColRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDFColRule_seq;

CREATE TABLE listDFColRule (
  idDFC int NOT NULL DEFAULT NEXTVAL ('listDFColRule_seq'),
  idApp int NOT NULL,
  idCol int NOT NULL,
  stat varchar(1) NOT NULL,
  listRule varchar(1) NOT NULL,
  formatRule varchar(1) NOT NULL,
  refRule varchar(1) NOT NULL,
  PRIMARY KEY (idDFC)
 ,
  CONSTRAINT listDFColRule_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listDFColRule_ibfk_1 ON listDFColRule (idApp);

--
-- Table structure for table listDFColSpecRules
--

DROP TABLE IF EXISTS listDFColSpecRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDFColSpecRules_seq;

CREATE TABLE listDFColSpecRules (
  idlistDFColSpecRules int NOT NULL DEFAULT NEXTVAL ('listDFColSpecRules_seq'),
  idDFC int NOT NULL,
  ruleType varchar(45) NOT NULL,
  leftExp varchar(45) NOT NULL,
  doOperation varchar(45) NOT NULL,
  rightExp varchar(45) NOT NULL,
  PRIMARY KEY (idlistDFColSpecRules)
 ,
  CONSTRAINT listdfcsr_ibfk_1 FOREIGN KEY (idDFC) REFERENCES listDFColRule (idDFC) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listdfcsr_ibfk_1 ON listDFColSpecRules (idDFC);

--
-- Table structure for table listDFSetRule
--

DROP TABLE IF EXISTS listDFSetRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDFSetRule_seq;

CREATE TABLE listDFSetRule (
  idDFSet int NOT NULL DEFAULT NEXTVAL ('listDFSetRule_seq'),
  idApp int NOT NULL,
  count varchar(1) NOT NULL,
  sum varchar(1) NOT NULL,
  correlation varchar(1) NOT NULL,
  statisticalParam varchar(1) DEFAULT NULL,
  duplicateFile varchar(50) DEFAULT NULL,
  PRIMARY KEY (idDFSet)
 ,
  CONSTRAINT listDFS_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDFSetRule_seq RESTART WITH 300;

CREATE INDEX listDFS_ibfk_1 ON listDFSetRule (idApp);

--
-- Table structure for table listDFSetComparisonRule
--

DROP TABLE IF EXISTS listDFSetComparisonRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDFSetComparisonRule_seq;

CREATE TABLE listDFSetComparisonRule (
  idlistDFSetComparisonRule int NOT NULL DEFAULT NEXTVAL ('listDFSetComparisonRule_seq'),
  idDFSet int NOT NULL,
  comparisonType varchar(45) NOT NULL,
  comparisonMethod varchar(45) NOT NULL,
  comparisonDuration int NOT NULL,
  threshold int NOT NULL,
  PRIMARY KEY (idlistDFSetComparisonRule)
 ,
  CONSTRAINT listDFSCR_ibfk_1 FOREIGN KEY (idDFSet) REFERENCES listDFSetRule (idDFSet) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDFSetComparisonRule_seq RESTART WITH 300;

CREATE INDEX listDFSCR_ibfk_1 ON listDFSetComparisonRule (idDFSet);

--
-- Table structure for table listDFTranRule
--

DROP TABLE IF EXISTS listDFTranRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDFTranRule_seq;

CREATE TABLE listDFTranRule (
  idDFT int NOT NULL DEFAULT NEXTVAL ('listDFTranRule_seq'),
  idApp int NOT NULL,
  dupRow varchar(1) NOT NULL,
  seqRow varchar(1) NOT NULL,
  seqIDcol int NOT NULL,
  threshold double precision NOT NULL DEFAULT '0',
  type varchar(50) NOT NULL,
  PRIMARY KEY (idDFT)
)  ;

ALTER SEQUENCE listDFTranRule_seq RESTART WITH 579;

--
-- Table structure for table listDMRules
--

DROP TABLE IF EXISTS listDMRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDMRules_seq;

CREATE TABLE listDMRules (
  idDM int NOT NULL DEFAULT NEXTVAL ('listDMRules_seq'),
  idApp int NOT NULL,
  matchType varchar(45) NOT NULL,
  matchType2 varchar(45) NOT NULL,
  PRIMARY KEY (idDM)
 ,
  CONSTRAINT listDMRules_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDMRules_seq RESTART WITH 26;

CREATE INDEX listDMRules_ibfk_1 ON listDMRules (idApp);

--
-- Table structure for table listDMCriteria
--

DROP TABLE IF EXISTS listDMCriteria;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDMCriteria_seq;

CREATE TABLE listDMCriteria (
  idlistDMCriteria int NOT NULL DEFAULT NEXTVAL ('listDMCriteria_seq'),
  idDM int NOT NULL,
  leftSideExp varchar(500) NOT NULL DEFAULT '',
  rightSideExp varchar(500) NOT NULL DEFAULT '',
  idLeftColumn int DEFAULT NULL,
  leftSideColumn varchar(500) DEFAULT NULL,
  idRightColumn int DEFAULT NULL,
  rightSideColumn varchar(500) DEFAULT NULL,
  PRIMARY KEY (idlistDMCriteria),
  CONSTRAINT listDMCriteria_ibfk_1 FOREIGN KEY (idDM) REFERENCES listDMRules (idDM) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDMCriteria_seq RESTART WITH 28;

CREATE INDEX listDMCriteria_ibfk_1 ON listDMCriteria (idDM);

--
-- Table structure for table listDataAccess
--

DROP TABLE IF EXISTS listDataAccess;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataAccess_seq;

CREATE TABLE listDataAccess (
  idlistDataAccess int NOT NULL DEFAULT NEXTVAL ('listDataAccess_seq'),
  idData int NOT NULL,
  hostName varchar(500) NOT NULL,
  portName varchar(45) NOT NULL,
  userName text NOT NULL,
  pwd text NOT NULL,
  schemaName text NOT NULL,
  folderName text NOT NULL,
  queryString text,
  query text NOT NULL,
  incrementalType varchar(10) DEFAULT NULL,
  dateFormat varchar(50) DEFAULT NULL,
  sliceStart varchar(10) DEFAULT NULL,
  sliceEnd varchar(10) DEFAULT NULL,
  idDataSchema int NOT NULL,
  whereCondition varchar(500) DEFAULT NULL,
  domain varchar(50) DEFAULT NULL,
  fileHeader varchar(10) DEFAULT 'Y',
  metaData varchar(1000) DEFAULT NULL,
  isRawData varchar(20) DEFAULT NULL,
  sslEnb varchar(10) DEFAULT NULL,
  sslTrustStorePath varchar(200) DEFAULT NULL,
  trustPassword varchar(100) DEFAULT NULL,
  hivejdbchost varchar(45) DEFAULT NULL,
  hivejdbcport varchar(45) DEFAULT NULL,
  gatewayPath varchar(250) DEFAULT NULL,
  jksPath varchar(250) DEFAULT NULL,
  zookeeperUrl varchar(250) DEFAULT NULL,
  rollingHeader varchar(50) DEFAULT 'N',
  rollingColumn varchar(50) DEFAULT NULL,
  historicDateTable varchar(2000) DEFAULT NULL,
  PRIMARY KEY (idlistDataAccess),
  CONSTRAINT listdataaccess_ibfk_1 FOREIGN KEY (idData) REFERENCES listDataSources (idData) ON DELETE CASCADE ON UPDATE CASCADE
)  ;

ALTER SEQUENCE listDataAccess_seq RESTART WITH 288;

CREATE INDEX listda_ibfk_1 ON listDataAccess (idData);

--
-- Table structure for table listDataBlendColDefinitions
--

DROP TABLE IF EXISTS listDataBlendColDefinitions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataBlendColDefinitions_seq;

CREATE TABLE listDataBlendColDefinitions (
  idCol int NOT NULL DEFAULT NEXTVAL ('listDataBlendColDefinitions_seq'),
  name varchar(45) NOT NULL,
  idDataBlend int NOT NULL,
  colExpression varchar(200) NOT NULL,
  PRIMARY KEY (idCol)
 ,
  CONSTRAINT listDataBlendcoldefinitions_ibfk_1 FOREIGN KEY (idDataBlend) REFERENCES listDataBlend (idDataBlend) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listdbcd_ibfk_1 ON listDataBlendColDefinitions (idDataBlend);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table listDataBlendFilterDefinitions
--

DROP TABLE IF EXISTS listDataBlendFilterDefinitions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataBlendFilterDefinitions_seq;

CREATE TABLE listDataBlendFilterDefinitions (
  idFilter int NOT NULL DEFAULT NEXTVAL ('listDataBlendFilterDefinitions_seq'),
  idDataBlend int NOT NULL,
  name varchar(45) NOT NULL,
  filteringExp varchar(200) NOT NULL,
  PRIMARY KEY (idFilter)
 ,
  CONSTRAINT ldbfd_ibfk_1 FOREIGN KEY (idDataBlend) REFERENCES listDataBlend (idDataBlend) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX ldbfd_ibfk_1 ON listDataBlendFilterDefinitions (idDataBlend);

--
-- Table structure for table listDataBlendRowAdd
--

DROP TABLE IF EXISTS listDataBlendRowAdd;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataBlendRowAdd_seq;

CREATE TABLE listDataBlendRowAdd (
  idRowAdd int NOT NULL DEFAULT NEXTVAL ('listDataBlendRowAdd_seq'),
  idDataBlend int NOT NULL,
  rowAddExpression varchar(200) NOT NULL,
  PRIMARY KEY (idRowAdd)
 ,
  CONSTRAINT listDataBlendRowAdd_ibfk_1 FOREIGN KEY (idDataBlend) REFERENCES listDataBlend (idDataBlend) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX idDataBlend ON listDataBlendRowAdd (idDataBlend);

--
-- Table structure for table listDataDefinition
--

DROP TABLE IF EXISTS listDataDefinition;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataDefinition_seq;

CREATE TABLE listDataDefinition (
  idColumn int NOT NULL DEFAULT NEXTVAL ('listDataDefinition_seq'),
  idData int NOT NULL,
  columnName varchar(45) NOT NULL,
  displayName varchar(200) NOT NULL,
  format varchar(45) DEFAULT NULL,
  hashValue varchar(1) NOT NULL DEFAULT 'N',
  numericalStat varchar(1) NOT NULL DEFAULT 'N',
  stringStat varchar(1) NOT NULL DEFAULT 'N',
  nullCountThreshold double precision DEFAULT '0',
  numericalThreshold double precision DEFAULT '0',
  stringStatThreshold double precision DEFAULT '0',
  KBE varchar(1) DEFAULT 'N',
  dgroup varchar(1) DEFAULT 'N',
  dupkey varchar(1) DEFAULT 'N',
  measurement varchar(1) DEFAULT 'N',
  blend varchar(1) NOT NULL DEFAULT 'N',
  idCol int DEFAULT NULL,
  incrementalCol varchar(10) NOT NULL DEFAULT 'N',
  idDataSchema int NOT NULL DEFAULT '0',
  nonNull varchar(20) NOT NULL DEFAULT 'N',
  primaryKey varchar(20) NOT NULL DEFAULT 'N',
  recordAnomaly varchar(20) NOT NULL DEFAULT 'N',
  recordAnomalyThreshold double precision DEFAULT '0',
  dataDrift varchar(10) NOT NULL DEFAULT 'N',
  dataDriftThreshold double precision NOT NULL DEFAULT '0',
  outOfNormStat varchar(50) DEFAULT 'N',
  outOfNormStatThreshold double precision NOT NULL DEFAULT '0',
  isMasked varchar(10) DEFAULT NULL,
  partitionBy varchar(10) DEFAULT 'N',
  lengthCheck varchar(45) DEFAULT NULL,
  lengthValue varchar(100) DEFAULT NULL,
  applyrule varchar(45) DEFAULT 'N',
  startDate varchar(5) DEFAULT NULL,
  timelinessKey varchar(5) DEFAULT NULL,
  endDate varchar(5) DEFAULT NULL,
  defaultCheck varchar(12) DEFAULT NULL,
  defaultValues varchar(12) DEFAULT NULL,
  patternCheck varchar(10) DEFAULT 'N',
  patterns varchar(500) DEFAULT NULL,
  dateRule varchar(5) DEFAULT NULL,
  badData varchar(5) DEFAULT NULL,
  dateFormat varchar(50) DEFAULT NULL,
  correlationcolumn varchar(10) DEFAULT NULL,
  lengthCheckThreshold double precision DEFAULT '0',
  badDataCheckThreshold double precision DEFAULT '0',
  patternCheckThreshold double precision DEFAULT '0',
  maxLengthCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatternCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatterns text,
  PRIMARY KEY (idColumn)
)  ;

ALTER SEQUENCE listDataDefinition_seq RESTART WITH 6745;

CREATE INDEX listdd_ibfk_1 ON listDataDefinition (idData);

--
-- Table structure for table listDataFiles
--

DROP TABLE IF EXISTS listDataFiles;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataFiles_seq;

CREATE TABLE listDataFiles (
  idDataFile int NOT NULL DEFAULT NEXTVAL ('listDataFiles_seq'),
  idData int NOT NULL,
  fileName varchar(500) NOT NULL,
  PRIMARY KEY (idDataFile)
) ;

--
-- Table structure for table listDataSchema
--

DROP TABLE IF EXISTS listDataSchema;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDataSchema_seq;

CREATE TABLE listDataSchema (
  idDataSchema int NOT NULL DEFAULT NEXTVAL ('listDataSchema_seq'),
  schemaName varchar(500) DEFAULT NULL,
  schemaType varchar(500) DEFAULT NULL,
  ipAddress varchar(255) NOT NULL,
  databaseSchema varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  port varchar(255) NOT NULL,
  domain varchar(500) DEFAULT NULL,
  gss_jaas varchar(500) DEFAULT NULL,
  krb5conf varchar(500) DEFAULT NULL,
  autoGenerate varchar(10) DEFAULT 'N',
  suffixes varchar(1000) DEFAULT NULL,
  prefixes varchar(1000) DEFAULT NULL,
  createdAt timestamp(0) NOT NULL,
  updatedAt timestamp(0) NOT NULL,
  createdBy int NOT NULL,
  updatedBy int NOT NULL,
  project_id int DEFAULT NULL,
  sslEnb varchar(10) DEFAULT NULL,
  sslTrustStorePath varchar(200) DEFAULT NULL,
  trustPassword varchar(100) DEFAULT NULL,
  hivejdbcport varchar(45) DEFAULT NULL,
  hivejdbchost varchar(45) DEFAULT NULL,
  Action varchar(100) DEFAULT NULL,
  gatewayPath varchar(250) DEFAULT NULL,
  jksPath varchar(250) DEFAULT NULL,
  zookeeperUrl varchar(250) DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  folderPath varchar(1000) DEFAULT NULL,
  fileNamePattern varchar(250) DEFAULT NULL,
  fileDataFormat varchar(250) DEFAULT NULL,
  headerPresent varchar(10) DEFAULT NULL,
  headerFilePath varchar(1000) DEFAULT NULL,
  headerFileNamePattern varchar(250) DEFAULT NULL,
  headerFileDataFormat varchar(250) DEFAULT NULL,
  bucketName varchar(500) DEFAULT NULL,
  accessKey varchar(1000) DEFAULT NULL,
  secretKey varchar(1000) DEFAULT NULL,
  idSORs int DEFAULT NULL,
  bigQueryProjectName varchar(2000) DEFAULT NULL,
  privateKeyId varchar(2000) DEFAULT NULL,
  privateKey varchar(2500) DEFAULT NULL,
  clientId varchar(2000) DEFAULT NULL,
  clientEmail varchar(2000) DEFAULT NULL,
  datasetName varchar(2000) DEFAULT NULL,
  azureClientId text,
  azureClientSecret text,
  azureTenantId text,
  azureServiceURI text,
  azureFilePath text,
  partitionedFolders varchar(10) DEFAULT 'N',
  enableFileMonitoring varchar(10) DEFAULT 'N',
  multiPattern varchar(10) DEFAULT 'N',
  startingUniqueCharCount int DEFAULT '0',
  endingUniqueCharCount int DEFAULT '0',
  maxFolderDepth int DEFAULT '2',
  fileEncrypted varchar(10) DEFAULT 'N',
  domain_id int NOT NULL DEFAULT '0',
  singleFile varchar(10) DEFAULT 'N',
  externalfileNamePattern varchar(10) DEFAULT 'N',
  externalfileName varchar(225) DEFAULT NULL,
  patternColumn varchar(225) DEFAULT NULL,
  headerColumn varchar(225) DEFAULT NULL,
  localDirectoryColumnIndex varchar(100) DEFAULT NULL,
  xsltFolderPath varchar(100) DEFAULT NULL,
  kmsAuthDisabled varchar(10) DEFAULT 'Y',
  readLatestPartition varchar(10) DEFAULT 'N',
  alation_integration_enabled varchar(10) DEFAULT 'N',
  incremental_dataread_enabled varchar(10) DEFAULT 'N',
  cluster_property_category varchar(255) DEFAULT 'cluster',
  multiFolderEnabled varchar(10) DEFAULT 'N',
  PRIMARY KEY (idDataSchema)
)  ;

ALTER SEQUENCE listDataSchema_seq RESTART WITH 56;

CREATE INDEX dbPer_listDataSchema ON listDataSchema (project_id);

--
-- Table structure for table listDerivedDataSources
--

DROP TABLE IF EXISTS listDerivedDataSources;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listDerivedDataSources_seq;

CREATE TABLE listDerivedDataSources (
  idDerivedData int NOT NULL DEFAULT NEXTVAL ('listDerivedDataSources_seq'),
  idData int NOT NULL,
  name varchar(1000) NOT NULL DEFAULT '',
  description varchar(1000) NOT NULL,
  template1Name varchar(1000) DEFAULT NULL,
  template1IdData int DEFAULT NULL,
  template1AliasName varchar(200) DEFAULT NULL,
  template2Name varchar(1000) DEFAULT NULL,
  template2IdData int DEFAULT NULL,
  template2AliasName varchar(200) DEFAULT NULL,
  queryText varchar(10000) DEFAULT NULL,
  createdBy int NOT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  updatedBy int DEFAULT NULL,
  project_id int DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  PRIMARY KEY (idDerivedData)
 ,
  CONSTRAINT listDerivedDataSources_idfk_1 FOREIGN KEY (idData) REFERENCES listDataSources (idData) ON DELETE CASCADE
) ;

CREATE INDEX listDerivedDataSources_idfk_1 ON listDerivedDataSources (idData);

--
-- Table structure for table listExceptionMessage
--

DROP TABLE IF EXISTS listExceptionMessage;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listExceptionMessage_seq;

CREATE TABLE listExceptionMessage (
  idResults int NOT NULL DEFAULT NEXTVAL ('listExceptionMessage_seq'),
  idApp int NOT NULL,
  idRule int NOT NULL,
  sourceName varchar(45) NOT NULL,
  message varchar(200) NOT NULL,
  PRIMARY KEY (idResults)
 ,
  CONSTRAINT listExceptionMessage_ibfk_1 FOREIGN KEY (idApp) REFERENCES listApplications (idApp) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listExceptionMessage_ibfk_1 ON listExceptionMessage (idApp);

--
-- Table structure for table listFMRules
--

DROP TABLE IF EXISTS listFMRules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listFMRules_seq;

CREATE TABLE listFMRules (
  idFM int NOT NULL DEFAULT NEXTVAL ('listFMRules_seq'),
  idApp int NOT NULL,
  dupCheck varchar(10) DEFAULT NULL,
  filter varchar(255) DEFAULT NULL,
  PRIMARY KEY (idFM)
) ;

--
-- Table structure for table listGlobalThresholds
--

DROP TABLE IF EXISTS listGlobalThresholds;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listGlobalThresholds_seq;

CREATE TABLE listGlobalThresholds (
  idGlobalThreshold int NOT NULL DEFAULT NEXTVAL ('listGlobalThresholds_seq'),
  domainId int NOT NULL,
  globalColumnName varchar(200) NOT NULL,
  description varchar(1000) DEFAULT NULL,
  nullCountThreshold double precision NOT NULL DEFAULT '0',
  numericalThreshold double precision NOT NULL DEFAULT '0',
  stringStatThreshold double precision NOT NULL DEFAULT '0',
  dataDriftThreshold double precision NOT NULL DEFAULT '0',
  recordAnomalyThreshold double precision NOT NULL DEFAULT '0',
  lengthCheckThreshold double precision NOT NULL DEFAULT '0',
  badDataCheckThreshold double precision NOT NULL DEFAULT '0',
  patternCheckThreshold double precision NOT NULL DEFAULT '0',
  PRIMARY KEY (idGlobalThreshold),
  CONSTRAINT threshold_name UNIQUE (domainId,globalColumnName)
) ;

--
-- Table structure for table listGlobalThresholdsSelected
--

DROP TABLE IF EXISTS listGlobalThresholdsSelected;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listGlobalThresholdsSelected_seq;

CREATE TABLE listGlobalThresholdsSelected (
  idGlobalThresholdSelected int NOT NULL DEFAULT NEXTVAL ('listGlobalThresholdsSelected_seq'),
  idGlobalThreshold int NOT NULL,
  idData int NOT NULL,
  idColumn int NOT NULL,
  PRIMARY KEY (idGlobalThresholdSelected)
) ;

--
-- Table structure for table listModelGovernance
--

DROP TABLE IF EXISTS listModelGovernance;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listModelGovernance_seq;

CREATE TABLE listModelGovernance (
  idModel int NOT NULL DEFAULT NEXTVAL ('listModelGovernance_seq'),
  idApp int NOT NULL,
  modelGovernanceType varchar(50) DEFAULT NULL,
  modelIdCol varchar(50) DEFAULT NULL,
  decileCol varchar(50) DEFAULT NULL,
  expectedPercentage double precision NOT NULL DEFAULT '0',
  thresholdPercentage double precision NOT NULL DEFAULT '0',
  leftSourceSliceStart varchar(50) DEFAULT NULL,
  leftSourceSliceEnd varchar(50) DEFAULT NULL,
  rightSourceSliceStart varchar(50) DEFAULT NULL,
  rightSourceSliceEnd varchar(50) DEFAULT NULL,
  measurementExpression varchar(1000) DEFAULT NULL,
  matchingExpression varchar(1000) DEFAULT NULL,
  PRIMARY KEY (idModel)
) ;

--
-- Table structure for table listOfSORs
--

DROP TABLE IF EXISTS listOfSORs;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listOfSORs_seq;

CREATE TABLE listOfSORs (
  idSORs int NOT NULL DEFAULT NEXTVAL ('listOfSORs_seq'),
  sorName varchar(100) DEFAULT NULL,
  projectId int NOT NULL,
  PRIMARY KEY (idSORs),
  CONSTRAINT sorName UNIQUE (sorName)
) ;

--
-- Table structure for table listOutlierRule
--

DROP TABLE IF EXISTS listOutlierRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listOutlierRule_seq;

CREATE TABLE listOutlierRule (
  idlistStdDevRules int NOT NULL DEFAULT NEXTVAL ('listOutlierRule_seq'),
  idDA int NOT NULL,
  idCol int NOT NULL,
  dmethod varchar(45) NOT NULL,
  threshold varchar(45) NOT NULL,
  averageRange varchar(45) DEFAULT NULL,
  PRIMARY KEY (idlistStdDevRules)
 ,
  CONSTRAINT listOutlierRule_ibfk_1 FOREIGN KEY (idDA) REFERENCES listDAStandardRules (idDA) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listOutlierRule_ibfk_1 ON listOutlierRule (idDA);

--
-- Table structure for table listParameters
--

DROP TABLE IF EXISTS listParameters;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listParameters_seq;

CREATE TABLE listParameters (
  idlistParameters int NOT NULL DEFAULT NEXTVAL ('listParameters_seq'),
  idResults int NOT NULL,
  parameter varchar(45) NOT NULL,
  value varchar(45) NOT NULL,
  PRIMARY KEY (idlistParameters)
 ,
  CONSTRAINT listParameters_ibfk_1 FOREIGN KEY (idResults) REFERENCES listExceptionMessage (idResults) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX listParameters_ibfk_1 ON listParameters (idResults);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table listRefFunctions
--

DROP TABLE IF EXISTS listRefFunctions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listRefFunctions_seq;

CREATE TABLE listRefFunctions (
  idFunctions int NOT NULL DEFAULT NEXTVAL ('listRefFunctions_seq'),
  name varchar(45) NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (idFunctions)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table listStatisticalMatchingConfig
--

DROP TABLE IF EXISTS listStatisticalMatchingConfig;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listStatisticalMatchingConfig_seq;

CREATE TABLE listStatisticalMatchingConfig (
  idStat int NOT NULL DEFAULT NEXTVAL ('listStatisticalMatchingConfig_seq'),
  idApp int NOT NULL,
  leftSideExp varchar(500) NOT NULL,
  rightSideExp varchar(500) NOT NULL,
  recordCountType varchar(20) DEFAULT NULL,
  recordCountThreshold double precision DEFAULT '0',
  measurementSum varchar(1) DEFAULT 'N',
  measurementSumType varchar(20) DEFAULT NULL,
  measurementSumThreshold double precision DEFAULT '0',
  measurementMean varchar(1) DEFAULT 'N',
  measurementMeanType varchar(20) DEFAULT NULL,
  measurementMeanThreshold double precision DEFAULT '0',
  measurementStdDev varchar(1) DEFAULT 'N',
  measurementStdDevType varchar(20) DEFAULT NULL,
  measurementStdDevThreshold double precision DEFAULT '0',
  groupBy varchar(1) DEFAULT 'N',
  PRIMARY KEY (idStat)
) ;

--
-- Table structure for table listTrigger
--

DROP TABLE IF EXISTS listTrigger;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE listTrigger_seq;

CREATE TABLE listTrigger (
  idlistTrigger int NOT NULL DEFAULT NEXTVAL ('listTrigger_seq'),
  appid int NOT NULL,
  appid2 int NOT NULL,
  PRIMARY KEY (idlistTrigger)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table list_batch_schema
--

DROP TABLE IF EXISTS list_batch_schema;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE list_batch_schema_seq;

CREATE TABLE list_batch_schema (
  idBatchSchema int NOT NULL DEFAULT NEXTVAL ('list_batch_schema_seq'),
  schemaBatchName text NOT NULL,
  schemaBatchType text NOT NULL,
  batchFileLocation text NOT NULL,
  totalSchemas int DEFAULT NULL,
  completedSchemas int DEFAULT NULL,
  idDataSchemas text,
  PRIMARY KEY (idBatchSchema)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table locationMapping
--

DROP TABLE IF EXISTS locationMapping;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE locationMapping_seq;

CREATE TABLE locationMapping (
  id int NOT NULL DEFAULT NEXTVAL ('locationMapping_seq'),
  locationId int NOT NULL,
  idApp int NOT NULL,
  sourceType varchar(1000) DEFAULT NULL,
  source varchar(1000) DEFAULT NULL,
  fileName varchar(1000) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE locationMapping_seq RESTART WITH 5;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table locations
--

DROP TABLE IF EXISTS locations;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE locations_seq;

CREATE TABLE locations (
  id int NOT NULL DEFAULT NEXTVAL ('locations_seq'),
  locationName varchar(100) DEFAULT NULL,
  projectId int DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE locations_seq RESTART WITH 4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table logging_activity
--

DROP TABLE IF EXISTS logging_activity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE logging_activity_seq;

CREATE TABLE logging_activity (
  row_id int NOT NULL DEFAULT NEXTVAL ('logging_activity_seq'),
  user_id int NOT NULL,
  user_name varchar(500) NOT NULL,
  access_url varchar(50) NOT NULL,
  databuck_feature varchar(255) DEFAULT NULL,
  session_id varchar(255) DEFAULT NULL,
  activity_log_time varchar(40) NOT NULL,
  PRIMARY KEY (row_id)
) ;


DROP TABLE IF EXISTS databuck_login_access_logs;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE databuck_login_access_logs_seq;

CREATE TABLE databuck_login_access_logs (
  row_id int NOT NULL DEFAULT NEXTVAL ('databuck_login_access_logs_seq'),
  user_id int NULL,
  user_name varchar(500) NOT NULL,
  access_url varchar(1000) NOT NULL,
  databuck_feature varchar(255) DEFAULT NULL,
  session_id varchar(255) DEFAULT NULL,
  activity_log_time varchar(40) NOT NULL,
  login_status  varchar(10) not null,
  PRIMARY KEY (row_id)
) ;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table login_group
--

DROP TABLE IF EXISTS login_group;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE login_group_seq;

CREATE TABLE login_group (
  row_id int NOT NULL DEFAULT NEXTVAL ('login_group_seq'),
  group_name varchar(255) DEFAULT NULL,
  is_approver smallint DEFAULT 0,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_group UNIQUE (group_name)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table login_group_to_project
--

DROP TABLE IF EXISTS login_group_to_project;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE login_group_to_project_seq;

CREATE TABLE login_group_to_project (
  row_id int NOT NULL DEFAULT NEXTVAL ('login_group_to_project_seq'),
  login_group_row_id int NOT NULL,
  project_row_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_project UNIQUE (login_group_row_id,project_row_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table login_group_to_role
--

DROP TABLE IF EXISTS login_group_to_role;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE login_group_to_role_seq;

CREATE TABLE login_group_to_role (
  row_id int NOT NULL DEFAULT NEXTVAL ('login_group_to_role_seq'),
  login_group_row_id int NOT NULL,
  role_row_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_role UNIQUE (login_group_row_id,role_row_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table matching_key
--

DROP TABLE IF EXISTS matching_key;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE matching_key_seq;

CREATE TABLE matching_key (
  id int NOT NULL DEFAULT NEXTVAL ('matching_key_seq'),
  left_column varchar(255) NOT NULL,
  right_column varchar(255) NOT NULL,
  app_id int NOT NULL,
  match_type smallint DEFAULT '0',
  PRIMARY KEY (id)
 ,
  CONSTRAINT matching_key_ibfk_1 FOREIGN KEY (app_id) REFERENCES application (id)
)  ;

ALTER SEQUENCE matching_key_seq RESTART WITH 26;

CREATE INDEX index_app_id ON matching_key (app_id);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table notification_alert_api
--

DROP TABLE IF EXISTS notification_alert_api;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_alert_api_seq;

CREATE TABLE notification_alert_api (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_alert_api_seq'),
  topic_row_id int NOT NULL,
  alert_msg text NOT NULL,
  alert_msg_code varchar(1000) NOT NULL,
  alert_label varchar(1000) NOT NULL,
  parent_topic_row_id int NOT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT topic_row_id UNIQUE (topic_row_id)
)  ;

ALTER SEQUENCE notification_alert_api_seq RESTART WITH 11;

--
-- Dumping data for table notification_alert_api
--

/*!40000 ALTER TABLE notification_alert_api DISABLE KEYS */;
INSERT INTO notification_alert_api VALUES (1,1,'Connection successful','101','idDataSchema',24),(2,2,'Connection failed','102','idDataSchema',24),(3,3,'Template successful','103','idData',25),(4,4,'Template failed','104','idData',25),(5,17,'Validation successful','105','idApp',26),(6,18,'Validation failed','4','idApp',26),(7,31,'RunConnection completed ','106','idDataSchema',31),(8,30,'RunConnection failed ','107','idDataSchema',30),(9,29,'RunAppGroup completed','108','idAppGroup',29),(10,28,'RunAppGroup failed','109','idAppGroup',28);
/*!40000 ALTER TABLE notification_alert_api ENABLE KEYS */;

--
-- Table structure for table notification_applicable_tags
--

DROP TABLE IF EXISTS notification_applicable_tags;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_applicable_tags_seq;

CREATE TABLE notification_applicable_tags (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_applicable_tags_seq'),
  topic_row_id int NOT NULL,
  tag_row_id int NOT NULL,
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_topic_tag_mapping UNIQUE (topic_row_id,tag_row_id)
)  ;

ALTER SEQUENCE notification_applicable_tags_seq RESTART WITH 42;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table notification_applicable_tags
--

/*!40000 ALTER TABLE notification_applicable_tags DISABLE KEYS */;
INSERT INTO notification_applicable_tags VALUES (1,24,1,1),(2,24,2,1),(3,24,3,1),(4,24,15,1),(5,24,16,1),(6,25,1,1),(7,25,4,1),(8,25,5,1),(9,25,7,1),(10,25,8,1),(11,25,15,1),(12,25,16,1),(13,26,1,1),(14,26,9,1),(15,26,10,1),(16,26,11,1),(17,26,12,1),(18,26,13,1),(19,26,15,1),(20,26,16,1),(21,26,14,1),(22,25,17,1),(23,25,18,1),(24,27,14,1),(25,27,16,1),(26,27,19,1),(27,28,1,1),(28,28,16,1),(29,28,15,1),(30,29,1,1),(31,29,16,1),(32,29,15,1),(33,30,1,1),(34,30,16,1),(35,30,15,1),(36,31,1,1),(37,31,16,1),(38,31,15,1),(39,32,1,1),(40,32,16,1),(41,32,20,1);
/*!40000 ALTER TABLE notification_applicable_tags ENABLE KEYS */;

--
-- Table structure for table notification_project_subscriptions
--

DROP TABLE IF EXISTS notification_project_subscriptions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_project_subscriptions_seq;

CREATE TABLE notification_project_subscriptions (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_project_subscriptions_seq'),
  topic_row_id int NOT NULL,
  project_row_id int NOT NULL,
  focus_type smallint NOT NULL,
  notification_email varchar(255) DEFAULT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_subcription UNIQUE (topic_row_id,focus_type,project_row_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table notification_tags_master
--

DROP TABLE IF EXISTS notification_tags_master;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_tags_master_seq;

CREATE TABLE notification_tags_master (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_tags_master_seq'),
  tag_id varchar(100) NOT NULL,
  tag_description varchar(255) DEFAULT '',
  active smallint NOT NULL DEFAULT 1,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_notification_tag UNIQUE (tag_id)
)  ;

ALTER SEQUENCE notification_tags_master_seq RESTART WITH 21;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table notification_tags_master
--

/*!40000 ALTER TABLE notification_tags_master DISABLE KEYS */;
INSERT INTO notification_tags_master VALUES (1,'FocusObjectId','', 1),(2,'ConnectionName','', 1),(3,'ConnectionType','', 1),(4,'DataTemplateName','', 1),(5,'DataTemplateDescription','', 1),(6,'DataLocation','', 1),(7,'DataSource','', 1),(8,'DataTableName','', 1),(9,'ValidationName','', 1),(10,'ValidationDescription','', 1),(11,'ValidationType','', 1),(12,'ValidationApplyRules','', 1),(13,'ValidationTemplateName','', 1),(14,'FileName','', 1),(15,'Status','', 1),(16,'User','', 1),(17,'ValidationCreateStatus','', 1),(18,'MicrosegValidationCreateStatus','', 1),(19,'msg','', 1),(20,'AlertEventMsg','', 1);
/*!40000 ALTER TABLE notification_tags_master ENABLE KEYS */;

--
-- Table structure for table notification_topic_versions
--

DROP TABLE IF EXISTS notification_topic_versions;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_topic_versions_seq;

CREATE TABLE notification_topic_versions (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_topic_versions_seq'),
  topic_row_id int NOT NULL,
  topic_version smallint DEFAULT NULL,
  is_selected smallint DEFAULT 0,
  is_email smallint DEFAULT 0,
  is_sms smallint DEFAULT 0,
  base_media_ids varchar(1000) DEFAULT NULL,
  message_subject varchar(1000) DEFAULT NULL,
  message_body text,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_topic_version UNIQUE (topic_row_id,topic_version)
)  ;

ALTER SEQUENCE notification_topic_versions_seq RESTART WITH 33;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table notification_topic_versions
--

/*!40000 ALTER TABLE notification_topic_versions DISABLE KEYS */;
INSERT INTO notification_topic_versions VALUES (1,1,0, 1,1,0,'','Connection - [{FocusObjectId}] creation status.','Hi {{User}},\nConnection {ConnName} is successfully created.'),(2,2,0, 1,1,0,'','Connection - [{FocusObjectId}] creation status.','Hi {{User}},\nFailed to create Connection {ConnName}.'),(3,3,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].'),(4,4,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].'),(5,5,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] Profiling ReRun status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].'),(6,6,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].\nValidation Check with Id [{idApp}] is created Successfully for this template !!'),(7,7,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].\nAutomatic Validation Check creation is not enabled for this request !!'),(8,8,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{status}] for DataTemplate with id - [ {FocusObjectId} ].\nFailed to create default Validation check for this template !!'),(9,9,0, 1,1,0,'','Validation -[{FocusObjectId}] creation status.','Hi {{User}},\nValidation {ValName} is successfully created.'),(10,10,0, 1,1,0,'','Validation -[{FocusObjectId}] creation status.','Hi {{User}},\nFailed to create Validation {ValName}.'),(11,11,0, 1,1,0,'','Validation Approval Process Notification.','Hi {{User}},\nValidation Application ID:  {FocusObjectId} is ready for Test and Waiting for your Approval.'),(12,12,0, 1,1,0,'','Failed file alert: {fileName}','Hi {{User}},\nBelow are the status details of file checks for the file [  {folderPath} /  {fileName} ]\nZeroSizeFileCheck: {ZeroSizeFileCheck}\nRecordLengthCheck: {RecordLengthCheck}\nColumnCountCheck: {ColumnCountCheck}\nColumnSequenceCheck: {ColumnSequenceCheck}'),(13,13,0, 1,1,0,'','{MaxDate}: Rule Summary For Rule Id [{FocusObjectId}]','Hi {{User}}, \nRule information of Rule Id [{FocusObjectId}] :\nFile Frequency: {FileFrequency}\nDay of check: {DayOfCheck}\nTime of check: {Time}\nFile Path: {FilePath}\nFile Pattern: {FilePattern}\n\nBelow are the Summary Details of Rule:\n\nExpected Files Count: {ExpectedCount}\nArrived Files Count: {ArrivedCount}\nMissing Files Count: {MissingCount}\nDuplicate Files Count: {DuplicateCount}'),(14,14,0, 1,1,0,'','Template not created for {ConnName}','Hi {{User}},\nTemplate was not created because of duplicate column names in config file. Config file path: {ConfigFilePath} Table name: {TableName} Column name(s): {Columns}'),(15,15,0, 1,1,0,'','Data Matching  Report for IdApp:[{FocusObjectId}]','Hi {{User}},\nStatus: Data Matching validation ran for {FocusObjectId} failed due to exception. Check logs for details.'),(16,16,0, 1,1,0,'','Data Matching  Report for IdApp:[{FocusObjectId}]','Hi {{User}},\nStatus: Data Matching validation ran for {FocusObjectId} failed due to exception. Check logs for details.'),(17,17,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Data quality validation ran for {fileName} successfully.\nData Quality Score:\n For more details click on this link\n<host:port>/databuck/dashboard_table?idApp={FocusObjectId}'),(18,18,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Data quality validation ran for {fileName} with idApp {FocusObjectId} failed.'),(19,19,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Data quality validation ran for idApps {sb} is still in progress. Please check'),(20,20,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Databuck Processing {status} for AppId {FocusObjectId}.'),(21,21,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Data Matching validation ran for {fileName} with idApp {FocusObjectId} and Application Name:: {appname} found unmatched.'),(22,22,0, 1,1,0,'','Job for IdApp:: [{FocusObjectId}] and Application Name:: {appname}','Hi {{User}},\nStatus: Data Matching validation ran for idApps {sb} and Application Name -{appname} is still in progress. Please check'),(23,23,0, 1,1,0,'','Row Summary for Validation APP ID : {FocusObjectId}.','Hi {{User}},\nPlease find Attached Consolidated Row Summary for Validation APP ID : {FocusObjectId}.'),(24,24,0, 1,1,0,'','Default Subject','Default Body'),(25,25,0, 1,1,0,'','DataTemplate - [{FocusObjectId}] creation status.','Hi {{User}},\nREPORT: Data Analysis and Profiling Status - [{Status}] for DataTemplate with id - [ {FocusObjectId} ].\nValidation (Non Microsegment) creation is {ValidationCreateStatus}.\nValidation (Microsegment) creation is {MicrosegValidationCreateStatus} '),(26,26,0, 1,1,0,'','Default Subject','Default Body'),(27,27,0, 1,1,0,'','Failed file alert: {FileName}.','Hi {{User}},\n{msg}.'),(28,28,0, 1,1,0,'','AppGroup - [{FocusObjectId}] execution status','Hi {{User}},\nREPORT: AppGroup job with id - [{FocusObjectId}] execution Status is - [{Status}].'),(29,29,0, 1,1,0,'','AppGroup - [{FocusObjectId}] execution status','Hi {{User}},\nREPORT: AppGroup job with id - [{FocusObjectId}] execution Status is - [{Status}].'),(30,30,0, 1,1,0,'','Schema Job - [{FocusObjectId}] execution status','Hi {{User}},\nREPORT: Schema job with id - [{FocusObjectId}] execution Status is - [{Status}].'),(31,31,0, 1,1,0,'','Schema Job - [{FocusObjectId}] execution status','Hi {{User}},\nREPORT: Schema job with id - [{FocusObjectId}] execution Status is - [{Status}].'),(32,32,0, 1,1,0,'','Validation - [{FocusObjectId}] Alert Event','Hi {{User}},\nValidation job with id - [{FocusObjectId}] alert event details: \n{AlertEventMsg}.');
/*!40000 ALTER TABLE notification_topic_versions ENABLE KEYS */;

--
-- Table structure for table notification_topics
--

DROP TABLE IF EXISTS notification_topics;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE notification_topics_seq;

CREATE TABLE notification_topics (
  row_id int NOT NULL DEFAULT NEXTVAL ('notification_topics_seq'),
  topic_title varchar(255) DEFAULT NULL,
  focus_type smallint DEFAULT NULL,
  active smallint DEFAULT 1,
  managed_by smallint DEFAULT 0,
  is_publish_externally smallint DEFAULT 1,
  publish_url_1 varchar(1000) DEFAULT NULL,
  publish_url_2 varchar(1000) DEFAULT NULL,
  "authorization" varchar(100) DEFAULT NULL,
  service_id varchar(100) DEFAULT NULL,
  password varchar(100) DEFAULT NULL,
  url2_authorization varchar(100) DEFAULT NULL,
  url2_service_id varchar(100) DEFAULT NULL,
  url2_password varchar(100) DEFAULT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT unique_topic_title UNIQUE (topic_title,focus_type)
)  ;

ALTER SEQUENCE notification_topics_seq RESTART WITH 33;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table notification_topics
--

/*!40000 ALTER TABLE notification_topics DISABLE KEYS */;
INSERT INTO notification_topics VALUES (1,'CREATE_CONNECTION_SUCCESS',0, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'CREATE_CONNECTION_FAILED',0, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'CREATE_TEMPLATE_SUCCESS',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'CREATE_TEMPLATE_FAILED',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'PROFILING_ENABLED',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'AUTO_VALIDATON_CREATION_SUCCESS',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'AUTO_VALIDATON_CREATION_DISABLED',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'AUTO_VALIDATON_CREATION_FAILED',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,'CREATE_VALIDATION_SUCCESS',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'CREATE_VALIDATION_FAILED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,'VALIDATION_APPROVAL',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,'FILE_MONITORING_FAILED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,'FILE_MONITORING_SUCCESS',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,'BATCH_VALIDATION_TEMPCREATION_FAILED',1, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,'DATA_MATCHING_EXE_FAILED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,'ROLL_DATA_MATCHING_VALIDATION_EXE_FAILED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,'DQ_VALIDATION_RUN_SUCCESS',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,'DQ_VALIDATION_RUN_FAILED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,'DQ_VALIDATION_RUN_INPROGRESS',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(20,'DQ_VALIDATION_RUN_PROCESSING',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,'DM_VALIDATION_RUN_UNMATCHED',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,'DM_VALIDATION_RUN_INPROGRESS',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,'EXCEPTION_REPORT',2, 1,1, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,'CREATE_CONNECTION_STATUS',0, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,'CREATE_TEMPLATE_STATUS',1, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,'VALIDATION_RUN_STATUS',2, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,'FILE_MONITORING_PROCESS_FAILED',2, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(28,'RUN_APPGROUP_FAILURE',3, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(29,'RUN_APPGROUP_COMPLETE',3, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(30,'RUN_SCHEMA_FAILURE',4, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(31,'RUN_SCHEMA_COMPLETE',4, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(32,'VALIDATION_OTHER_EVENTS',2, 1,0, 1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE notification_topics ENABLE KEYS */;

--
-- Table structure for table project_jobs_queue
--

DROP TABLE IF EXISTS project_jobs_queue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE project_jobs_queue_seq;

CREATE TABLE project_jobs_queue (
  queueId int NOT NULL DEFAULT NEXTVAL ('project_jobs_queue_seq'),
  projectId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  processId int DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  PRIMARY KEY (queueId)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table project_jobs_tracking
--

DROP TABLE IF EXISTS project_jobs_tracking;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE project_jobs_tracking_seq;

CREATE TABLE project_jobs_tracking (
  id int NOT NULL DEFAULT NEXTVAL ('project_jobs_tracking_seq'),
  projectId int NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  idDataSchema int NOT NULL,
  connection_uniqueId varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table projecttoActDiruser
--

DROP TABLE IF EXISTS projecttoActDiruser;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE projecttoActDiruser (
  idProject int DEFAULT NULL,
  idUser varchar(110) DEFAULT NULL,
  isOwner varchar(1) NOT NULL,
  CONSTRAINT fk5 FOREIGN KEY (idProject) REFERENCES project (idProject) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

CREATE INDEX fk5_idx ON projecttoActDiruser (idProject);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table projecttogroup
--

DROP TABLE IF EXISTS projecttogroup;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE projecttogroup (
  idProject int DEFAULT NULL,
  idGroup int DEFAULT NULL,
  isOwner varchar(1) NOT NULL,
  CONSTRAINT fk1 FOREIGN KEY (idProject) REFERENCES project (idProject) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk2 FOREIGN KEY (idGroup) REFERENCES projgroup (idGroup) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

CREATE INDEX fk1_idx ON projecttogroup (idProject);
CREATE INDEX fk2_idx ON projecttogroup (idGroup);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table projecttouser
--

DROP TABLE IF EXISTS projecttouser;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE projecttouser (
  idProject int DEFAULT NULL,
  idUser varchar(100) DEFAULT NULL,
  isOwner varchar(1) NOT NULL
 ,
  CONSTRAINT fk3 FOREIGN KEY (idProject) REFERENCES project (idProject) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

CREATE INDEX fk3_idx ON projecttouser (idProject);
CREATE INDEX fk4_idx ON projecttouser (idUser);

--
-- Table structure for table ruleFields
--

DROP TABLE IF EXISTS ruleFields;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ruleFields (
  rule_id int NOT NULL,
  usercolumns varchar(100) NOT NULL,
  possiblenames varchar(100) NOT NULL
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table ruleMapping
--

DROP TABLE IF EXISTS ruleMapping;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE ruleMapping_seq;

CREATE TABLE ruleMapping (
  idruleMap int NOT NULL DEFAULT NEXTVAL ('ruleMapping_seq'),
  viewName varchar(60) NOT NULL,
  description varchar(80) DEFAULT NULL,
  idListColrules varchar(1000) NOT NULL,
  idData varchar(1000) NOT NULL,
  idApp varchar(1000) NOT NULL,
  PRIMARY KEY (idruleMap)
)  ;

ALTER SEQUENCE ruleMapping_seq RESTART WITH 15;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table ruleTosynonym
--

DROP TABLE IF EXISTS ruleTosynonym;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE ruleTosynonym (
  rule_id int NOT NULL,
  synonym_id int NOT NULL
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table rule_Template_Mapping
--

DROP TABLE IF EXISTS rule_Template_Mapping;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE rule_Template_Mapping_seq;

CREATE TABLE rule_Template_Mapping (
  id int NOT NULL DEFAULT NEXTVAL ('rule_Template_Mapping_seq'),
  templateid int NOT NULL,
  ruleId int NOT NULL,
  ruleName varchar(2500) DEFAULT NULL,
  ruleExpression text,
  ruleType varchar(100) DEFAULT NULL,
  anchorColumns text,
  activeFlag varchar(10) DEFAULT 'Y',
  filter_condition text,
  matchingRules text,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE rule_Template_Mapping_seq RESTART WITH 12;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table runScheduledTasks
--

DROP TABLE IF EXISTS runScheduledTasks;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE runScheduledTasks_seq;

CREATE TABLE runScheduledTasks (
  id int NOT NULL DEFAULT NEXTVAL ('runScheduledTasks_seq'),
  idApp int NOT NULL,
  status varchar(50) DEFAULT 'started',
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  processId int DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  uniqueId varchar(1000) DEFAULT NULL,
  triggered_by varchar(2500) DEFAULT 'system',
  triggeredByHost varchar(2500) DEFAULT NULL,
  validationRunType varchar(100) DEFAULT 'full_load',
  incremental_file_name varchar(2500) DEFAULT 'null',
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE runScheduledTasks_seq RESTART WITH 119;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table runTemplateTasks
--

DROP TABLE IF EXISTS runTemplateTasks;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE runTemplateTasks_seq;

CREATE TABLE runTemplateTasks (
  id int NOT NULL DEFAULT NEXTVAL ('runTemplateTasks_seq'),
  idData int NOT NULL,
  status varchar(50) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  processId int DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  uniqueId varchar(2000) DEFAULT NULL,
  templateRunType varchar(2000) DEFAULT 'newtemplate',
  triggeredByHost varchar(2500) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE runTemplateTasks_seq RESTART WITH 343;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table runningtaskStatus
--

DROP TABLE IF EXISTS runningtaskStatus;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE runningtaskStatus_seq;

CREATE TABLE runningtaskStatus (
  id int NOT NULL DEFAULT NEXTVAL ('runningtaskStatus_seq'),
  idApp int NOT NULL,
  status varchar(50) DEFAULT NULL,
  start_at timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE runningtaskStatus_seq RESTART WITH 668;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table scheduledTasks
--

DROP TABLE IF EXISTS scheduledTasks;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE scheduledTasks_seq;

CREATE TABLE scheduledTasks (
  id int NOT NULL DEFAULT NEXTVAL ('scheduledTasks_seq'),
  idApp int DEFAULT NULL,
  idSchedule int NOT NULL,
  status varchar(20) NOT NULL,
  runDate date DEFAULT NULL,
  dateTime timestamp(0) DEFAULT NULL,
  project_id int DEFAULT NULL,
  idDataSchema int DEFAULT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table schema_jobs_queue
--

DROP TABLE IF EXISTS schema_jobs_queue;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE schema_jobs_queue_seq;

CREATE TABLE schema_jobs_queue (
  queueId int NOT NULL DEFAULT NEXTVAL ('schema_jobs_queue_seq'),
  idDataSchema int NOT NULL,
  uniqueId varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  processId int DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  startTime timestamp(0) DEFAULT NULL,
  endTime timestamp(0) DEFAULT NULL,
  healthCheck varchar(10) DEFAULT 'N',
  PRIMARY KEY (queueId)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table schema_jobs_tracking
--

DROP TABLE IF EXISTS schema_jobs_tracking;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE schema_jobs_tracking_seq;

CREATE TABLE schema_jobs_tracking (
  id int NOT NULL DEFAULT NEXTVAL ('schema_jobs_tracking_seq'),
  idDataSchema int NOT NULL,
  uniqueId varchar(2500) DEFAULT NULL,
  idData int NOT NULL,
  template_uniqueId varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table schema_multipattern_info
--

DROP TABLE IF EXISTS schema_multipattern_info;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE schema_multipattern_info_seq;

CREATE TABLE schema_multipattern_info (
  id int NOT NULL DEFAULT NEXTVAL ('schema_multipattern_info_seq'),
  idDataSchema int NOT NULL,
  idData int NOT NULL,
  filePattern text,
  subFolderName varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table schema_version
--

DROP TABLE IF EXISTS schema_version;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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

--
-- Table structure for table secure_API
--

DROP TABLE IF EXISTS secure_API;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE secure_API (
  accessTokenId text,
  secretAccessToken text
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table staging_dbk_file_monitor_rules
--

DROP TABLE IF EXISTS staging_dbk_file_monitor_rules;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE staging_dbk_file_monitor_rules_seq;

CREATE TABLE staging_dbk_file_monitor_rules (
  Id int NOT NULL DEFAULT NEXTVAL ('staging_dbk_file_monitor_rules_seq'),
  connection_id int NOT NULL,
  validation_id int NOT NULL,
  schema_name varchar(250) NOT NULL,
  table_name varchar(1000) NOT NULL,
  file_indicator varchar(50) NOT NULL,
  dayOfWeek varchar(50) NOT NULL,
  hourOfDay smallint DEFAULT NULL,
  expected_time smallint DEFAULT NULL,
  expected_file_count smallint DEFAULT NULL,
  start_hour smallint DEFAULT NULL,
  end_hour smallint DEFAULT NULL,
  frequency smallint DEFAULT NULL,
  rule_delta_type varchar(50) DEFAULT NULL,
  PRIMARY KEY (Id)
)  ;

ALTER SEQUENCE staging_dbk_file_monitor_rules_seq RESTART WITH 100;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table staging_listApplications
--

DROP TABLE IF EXISTS staging_listApplications;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE staging_listApplications (
  idApp int NOT NULL,
  name text NOT NULL,
  description text,
  appType varchar(45) NOT NULL,
  idData int NOT NULL,
  idRightData int DEFAULT NULL,
  createdBy int NOT NULL,
  createdAt timestamp(0) DEFAULT NULL,
  updatedAt timestamp(0) DEFAULT NULL,
  updatedBy int DEFAULT NULL,
  fileNameValidation varchar(20) NOT NULL,
  entityColumn varchar(100) NOT NULL,
  colOrderValidation varchar(20) NOT NULL,
  matchingThreshold double precision DEFAULT '0',
  nonNullCheck varchar(45) DEFAULT NULL,
  numericalStatCheck varchar(10) DEFAULT NULL,
  stringStatCheck varchar(10) DEFAULT NULL,
  recordAnomalyCheck varchar(10) DEFAULT NULL,
  incrementalMatching varchar(10) NOT NULL DEFAULT 'N',
  incrementalTimestamp timestamp(0) DEFAULT NULL,
  dataDriftCheck varchar(10) NOT NULL DEFAULT 'N',
  updateFrequency varchar(100) NOT NULL DEFAULT 'Never',
  frequencyDays int DEFAULT NULL,
  recordCountAnomaly varchar(10) NOT NULL DEFAULT 'N',
  recordCountAnomalyThreshold double precision NOT NULL DEFAULT '0',
  timeSeries varchar(500) NOT NULL DEFAULT 'None',
  keyGroupRecordCountAnomaly varchar(500) DEFAULT NULL,
  outOfNormCheck varchar(50) DEFAULT 'N',
  applyRules varchar(10) NOT NULL DEFAULT 'N',
  applyDerivedColumns varchar(10) NOT NULL DEFAULT 'N',
  csvDir varchar(500) DEFAULT NULL,
  groupEquality varchar(20) NOT NULL DEFAULT 'N',
  groupEqualityThreshold double precision DEFAULT '0',
  buildHistoricFingerPrint varchar(20) DEFAULT 'N',
  historicStartDate timestamp(0) DEFAULT NULL,
  historicEndDate timestamp(0) DEFAULT NULL,
  historicDateFormat varchar(200) DEFAULT NULL,
  active varchar(10) DEFAULT 'yes',
  lengthCheck varchar(45) DEFAULT NULL,
  correlationcheck varchar(10) DEFAULT 'N',
  project_id int DEFAULT NULL,
  timelinessKeyCheck varchar(5) DEFAULT NULL,
  defaultCheck varchar(12) DEFAULT NULL,
  defaultValues varchar(12) DEFAULT NULL,
  patternCheck varchar(10) DEFAULT 'N',
  dateRuleCheck varchar(5) DEFAULT NULL,
  badData varchar(5) DEFAULT NULL,
  idLeftData int DEFAULT NULL,
  prefix1 varchar(100) DEFAULT NULL,
  prefix2 varchar(100) DEFAULT NULL,
  dGroupNullCheck varchar(10) DEFAULT NULL,
  dGroupDateRuleCheck varchar(100) DEFAULT NULL,
  fuzzylogic varchar(5) DEFAULT NULL,
  fileMonitoringType varchar(200) DEFAULT NULL,
  createdByUser varchar(1000) DEFAULT NULL,
  validityThreshold double precision DEFAULT NULL,
  dGroupDataDriftCheck varchar(10) DEFAULT NULL,
  rollTargetSchemaId int DEFAULT NULL,
  thresholdsApplyOption int NOT NULL DEFAULT '0',
  continuousFileMonitoring varchar(10) DEFAULT 'N',
  rollType varchar(50) DEFAULT NULL,
  approve_status int DEFAULT NULL,
  approve_comments varchar(2000) DEFAULT NULL,
  approve_date timestamp(0) DEFAULT NULL,
  approve_by int DEFAULT NULL,
  domain_id int NOT NULL DEFAULT '0',
  subcribed_email_id varchar(1000) DEFAULT NULL,
  approver_name varchar(2500) DEFAULT NULL,
  data_domain_id smallint DEFAULT NULL,
  staging_approve_status int DEFAULT NULL,
  maxLengthCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatternCheck varchar(10) NOT NULL DEFAULT 'N',
  PRIMARY KEY (idApp),
  CONSTRAINT slapp_ibfk_1 FOREIGN KEY (idData) REFERENCES listDataSources (idData) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE INDEX slapp_ibfk_1 ON staging_listApplications (idData)
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table staging_listApplicationsRulesCatalog
--

DROP TABLE IF EXISTS staging_listApplicationsRulesCatalog;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE staging_listApplicationsRulesCatalog_seq;

CREATE TABLE staging_listApplicationsRulesCatalog (
  row_id int NOT NULL DEFAULT NEXTVAL ('staging_listApplicationsRulesCatalog_seq'),
  idApp int DEFAULT NULL,
  rule_reference int DEFAULT NULL,
  rule_code varchar(255) DEFAULT NULL,
  defect_code varchar(255) DEFAULT NULL,
  rule_type varchar(255) NOT NULL,
  column_name varchar(255) NOT NULL,
  rule_name text,
  rule_category varchar(255) NOT NULL,
  rule_expression text,
  matching_rules text,
  custom_or_global_ruleId int DEFAULT NULL,
  threshold_value double precision NOT NULL DEFAULT '0',
  review_comments varchar(2000) DEFAULT NULL,
  review_date timestamp(0) DEFAULT NULL,
  review_by varchar(255) DEFAULT NULL,
  activeFlag smallint DEFAULT NULL,
  dimension_id int DEFAULT NULL,
  agingCheckEnabled varchar(100) DEFAULT 'N',
  rule_description text,
  custom_or_global_rule_type varchar(2000) DEFAULT NULL,
  filter_condition text,
  PRIMARY KEY (row_id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table staging_listDFTranRule
--

DROP TABLE IF EXISTS staging_listDFTranRule;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE staging_listDFTranRule_seq;

CREATE TABLE staging_listDFTranRule (
  idDFT int NOT NULL DEFAULT NEXTVAL ('staging_listDFTranRule_seq'),
  idApp int NOT NULL,
  dupRow varchar(1) NOT NULL,
  seqRow varchar(1) NOT NULL,
  seqIDcol int NOT NULL,
  threshold double precision NOT NULL DEFAULT '0',
  type varchar(50) NOT NULL,
  PRIMARY KEY (idDFT)
)  ;

ALTER SEQUENCE staging_listDFTranRule_seq RESTART WITH 200;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table staging_listDataDefinition
--

DROP TABLE IF EXISTS staging_listDataDefinition;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE staging_listDataDefinition_seq;

CREATE TABLE staging_listDataDefinition (
  idColumn int NOT NULL DEFAULT NEXTVAL ('staging_listDataDefinition_seq'),
  idData int NOT NULL,
  columnName varchar(45) NOT NULL,
  displayName varchar(200) NOT NULL,
  format varchar(45) DEFAULT NULL,
  hashValue varchar(1) NOT NULL DEFAULT 'N',
  numericalStat varchar(1) NOT NULL DEFAULT 'N',
  stringStat varchar(1) NOT NULL DEFAULT 'N',
  nullCountThreshold double precision DEFAULT '0',
  numericalThreshold double precision DEFAULT '0',
  stringStatThreshold double precision DEFAULT '0',
  KBE varchar(1) DEFAULT 'N',
  dgroup varchar(1) DEFAULT 'N',
  dupkey varchar(1) DEFAULT 'N',
  measurement varchar(1) DEFAULT 'N',
  blend varchar(1) NOT NULL DEFAULT 'N',
  idCol int DEFAULT NULL,
  incrementalCol varchar(10) NOT NULL DEFAULT 'N',
  idDataSchema int NOT NULL DEFAULT '0',
  nonNull varchar(20) NOT NULL DEFAULT 'N',
  primaryKey varchar(20) NOT NULL DEFAULT 'N',
  recordAnomaly varchar(20) NOT NULL DEFAULT 'N',
  recordAnomalyThreshold double precision DEFAULT '0',
  dataDrift varchar(10) NOT NULL DEFAULT 'N',
  dataDriftThreshold double precision NOT NULL DEFAULT '0',
  outOfNormStat varchar(50) DEFAULT 'N',
  outOfNormStatThreshold double precision NOT NULL DEFAULT '0',
  isMasked varchar(10) DEFAULT NULL,
  partitionBy varchar(10) DEFAULT 'N',
  lengthCheck varchar(45) DEFAULT NULL,
  lengthValue varchar(100) DEFAULT NULL,
  applyrule varchar(45) DEFAULT 'N',
  correlationcolumn varchar(10) DEFAULT 'N',
  startDate varchar(5) DEFAULT NULL,
  timelinessKey varchar(5) DEFAULT NULL,
  endDate varchar(5) DEFAULT NULL,
  defaultCheck varchar(12) DEFAULT NULL,
  defaultValues varchar(12) DEFAULT NULL,
  patternCheck varchar(10) DEFAULT 'N',
  patterns varchar(500) DEFAULT NULL,
  badData varchar(5) DEFAULT 'N',
  dateRule varchar(5) DEFAULT 'N',
  dateFormat varchar(50) DEFAULT NULL,
  lengthCheckThreshold double precision DEFAULT '0',
  badDataCheckThreshold double precision DEFAULT '0',
  patternCheckThreshold double precision DEFAULT '0',
  maxLengthCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatternCheck varchar(10) NOT NULL DEFAULT 'N',
  defaultPatterns text,
  PRIMARY KEY (idColumn)
)  ;

ALTER SEQUENCE staging_listDataDefinition_seq RESTART WITH 6745;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table sub_task_status
--

DROP TABLE IF EXISTS sub_task_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE sub_task_status_seq;

CREATE TABLE sub_task_status (
  id int NOT NULL DEFAULT NEXTVAL ('sub_task_status_seq'),
  Date date DEFAULT NULL,
  idapp int DEFAULT NULL,
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
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table symbol
--

DROP TABLE IF EXISTS symbol;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE symbol_seq;

CREATE TABLE symbol (
  id int NOT NULL DEFAULT NEXTVAL ('symbol_seq'),
  symbol varchar(45) DEFAULT NULL,
  Type varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
)  ;

ALTER SEQUENCE symbol_seq RESTART WITH 268;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table symbol
--

/*!40000 ALTER TABLE symbol DISABLE KEYS */;
INSERT INTO symbol VALUES (1,'!','Operator'),(2,'%','Operator'),(3,'&','Operator'),(4,'*','Operator'),(5,'+','Operator'),(6,'-','Operator'),(7,'/','Operator'),(8,'<','Operator'),(9,'<=','Operator'),(10,'<=>','Operator'),(11,'=','Operator'),(12,'==','Operator'),(13,'>','Operator'),(14,'>=','Operator'),(15,'^','Operator'),(16,'abs','Function'),(17,'acos','Function'),(18,'add_months','Function'),(19,'and','Function'),(20,'approx_count_distinct','Function'),(21,'approx_percentile','Function'),(22,'array','Function'),(23,'array_contains','Function'),(24,'ascii','Function'),(25,'asin','Function'),(26,'assert_true','Function'),(27,'atan','Function'),(28,'atan2','Function'),(29,'avg','Function'),(30,'base64','Function'),(31,'int','Function'),(32,'bin','Function'),(33,'binary','Function'),(34,'bit_length','Function'),(35,'boolean','Function'),(36,'bround','Function'),(37,'cast','Function'),(38,'cbrt','Function'),(39,'ceil','Function'),(40,'ceiling','Function'),(41,'char','Function'),(42,'char_length','Function'),(43,'character_length','Function'),(44,'chr','Function'),(45,'coalesce','Function'),(46,'collect_list','Function'),(47,'collect_set','Function'),(48,'concat','Function'),(49,'concat_ws','Function'),(50,'conv','Function'),(51,'corr','Function'),(52,'cos','Function'),(53,'cosh','Function'),(54,'cot','Function'),(55,'count','Function'),(56,'count_min_sketch','Function'),(57,'covar_pop','Function'),(58,'covar_samp','Function'),(59,'crc32','Function'),(60,'cube','Function'),(61,'cume_dist','Function'),(62,'current_database','Function'),(63,'current_date','Function'),(64,'current_timestamp','Function'),(65,'date','Function'),(66,'date_add','Function'),(67,'date_format','Function'),(68,'date_sub','Function'),(69,'date_trunc','Function'),(70,'datediff','Function'),(71,'day','Function'),(72,'dayofmonth','Function'),(73,'dayofweek','Function'),(74,'dayofyear','Function'),(75,'decimal','Function'),(76,'decode','Function'),(77,'degrees','Function'),(78,'dense_rank','Function'),(79,'double','Function'),(80,'e','Function'),(81,'elt','Function'),(82,'encode','Function'),(83,'exp','Function'),(84,'explode','Function'),(85,'explode_outer','Function'),(86,'expm1','Function'),(87,'factorial','Function'),(88,'find_in_set','Function'),(89,'first','Function'),(90,'first_value','Function'),(91,'float','Function'),(92,'floor','Function'),(93,'format_number','Function'),(94,'format_string','Function'),(95,'from_json','Function'),(96,'from_unixtime','Function'),(97,'from_utc_timestamp','Function'),(98,'get_json_object','Function'),(99,'greatest','Function'),(100,'grouping','Function'),(101,'grouping_id','Function'),(102,'hash','Function'),(103,'hex','Function'),(104,'hour','Function'),(105,'hypot','Function'),(106,'if','Function'),(107,'ifnull','Function'),(108,'in','Function'),(109,'initcap','Function'),(110,'inline','Function'),(111,'inline_outer','Function'),(112,'input_file_block_length','Function'),(113,'input_file_block_start','Function'),(114,'input_file_name','Function'),(115,'instr','Function'),(116,'int','Function'),(117,'isnan','Function'),(118,'isnotnull','Function'),(119,'isnull','Function'),(120,'java_method','Function'),(121,'json_tuple','Function'),(122,'kurtosis','Function'),(123,'lag','Function'),(124,'last','Function'),(125,'last_day','Function'),(126,'last_value','Function'),(127,'lcase','Function'),(128,'lead','Function'),(129,'least','Function'),(130,'left','Function'),(131,'length','Function'),(132,'levenshtein','Function'),(133,'like','Function'),(134,'ln','Function'),(135,'locate','Function'),(136,'log','Function'),(137,'log10','Function'),(138,'log1p','Function'),(139,'log2','Function'),(140,'lower','Function'),(141,'lpad','Function'),(142,'ltrim','Function'),(143,'map','Function'),(144,'map_keys','Function'),(145,'map_values','Function'),(146,'max','Function'),(147,'md5','Function'),(148,'mean','Function'),(149,'min','Function'),(150,'minute','Function'),(151,'mod','Function'),(152,'monotonically_increasing_id','Function'),(153,'month','Function'),(154,'months_between','Function'),(155,'named_struct','Function'),(156,'nanvl','Function'),(157,'negative','Function'),(158,'next_day','Function'),(159,'not','Function'),(160,'now','Function'),(161,'ntile','Function'),(162,'nullif','Function'),(163,'nvl','Function'),(164,'nvl2','Function'),(165,'octet_length','Function'),(166,'or','Function'),(167,'parse_url','Function'),(168,'percent_rank','Function'),(169,'percentile','Function'),(170,'percentile_approx','Function'),(171,'pi','Function'),(172,'pmod','Function'),(173,'posexplode','Function'),(174,'posexplode_outer','Function'),(175,'position','Function'),(176,'positive','Function'),(177,'pow','Function'),(178,'power','Function'),(179,'printf','Function'),(180,'quarter','Function'),(181,'radians','Function'),(182,'rand','Function'),(183,'randn','Function'),(184,'rank','Function'),(185,'reflect','Function'),(186,'regexp_extract','Function'),(187,'regexp_replace','Function'),(188,'repeat','Function'),(189,'replace','Function'),(190,'reverse','Function'),(191,'right','Function'),(192,'rint','Function'),(193,'rlike','Function'),(194,'rollup','Function'),(195,'round','Function'),(196,'row_number','Function'),(197,'rpad','Function'),(198,'rtrim','Function'),(199,'second','Function'),(200,'sentences','Function'),(201,'sha','Function'),(202,'sha1','Function'),(203,'sha2','Function'),(204,'shiftleft','Function'),(205,'shiftright','Function'),(206,'shiftrightunsigned','Function'),(207,'sign','Function'),(208,'signum','Function'),(209,'sin','Function'),(210,'sinh','Function'),(211,'size','Function'),(212,'skewness','Function'),(213,'smallint','Function'),(214,'sort_array','Function'),(215,'soundex','Function'),(216,'space','Function'),(217,'spark_partition_id','Function'),(218,'split','Function'),(219,'sqrt','Function'),(220,'stack','Function'),(221,'std','Function'),(222,'stddev','Function'),(223,'stddev_pop','Function'),(224,'stddev_samp','Function'),(225,'str_to_map','Function'),(226,'string','Function'),(227,'struct','Function'),(228,'substr','Function'),(229,'substring','Function'),(230,'substring_index','Function'),(231,'sum','Function'),(232,'tan','Function'),(233,'tanh','Function'),(234,'timestamp','Function'),(235,'tinyint','Function'),(236,'to_date','Function'),(237,'to_json','Function'),(238,'to_timestamp','Function'),(239,'to_unix_timestamp','Function'),(240,'to_utc_timestamp','Function'),(241,'translate','Function'),(242,'trim','Function'),(243,'trunc','Function'),(244,'ucase','Function'),(245,'unbase64','Function'),(246,'unhex','Function'),(247,'unix_timestamp','Function'),(248,'upper','Function'),(249,'uuid','Function'),(250,'var_pop','Function'),(251,'var_samp','Function'),(252,'variance','Function'),(253,'weekofyear','Function'),(254,'when','Function'),(255,'window','Function'),(256,'xpath','Function'),(257,'xpath_boolean','Function'),(258,'xpath_double','Function'),(259,'xpath_float','Function'),(260,'xpath_int','Function'),(261,'xpath_long','Function'),(262,'xpath_number','Function'),(263,'xpath_short','Function'),(264,'xpath_string','Function'),(265,'year','Function'),(266,'|','Operator'),(267,'~','Operator');
/*!40000 ALTER TABLE symbol ENABLE KEYS */;

--
-- Table structure for table task_progress_status
--

DROP TABLE IF EXISTS task_progress_status;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE task_progress_status_seq;

CREATE TABLE task_progress_status (
  id int NOT NULL DEFAULT NEXTVAL ('task_progress_status_seq'),
  Date date DEFAULT NULL,
  idapp int DEFAULT NULL,
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
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table template_column_change_history
--

DROP TABLE IF EXISTS template_column_change_history;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE template_column_change_history (
  templateId int NOT NULL,
  columnName varchar(2500) NOT NULL,
  isNewColumn varchar(10) DEFAULT NULL,
  isMissingColumn varchar(10) DEFAULT NULL,
  changeDetectedTime timestamp(0) DEFAULT NULL,
  PRIMARY KEY (templateId,columnName)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table userRegistrationCode
--

DROP TABLE IF EXISTS userRegistrationCode;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE userRegistrationCode_seq;

CREATE TABLE userRegistrationCode (
  id int NOT NULL DEFAULT NEXTVAL ('userRegistrationCode_seq'),
  idUser int NOT NULL,
  email varchar(100) NOT NULL,
  code varchar(100) NOT NULL,
  createdAt timestamp(0) NOT NULL,
  alive smallint NOT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table user_token
--

DROP TABLE IF EXISTS user_token;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE user_token_seq;

CREATE TABLE user_token (
  row_id int NOT NULL DEFAULT NEXTVAL ('user_token_seq'),
  idUser int DEFAULT NULL,
  userName varchar(255) DEFAULT NULL,
  email varchar(255) NOT NULL,
  userRole int DEFAULT NULL,
  userRoleName varchar(255) DEFAULT NULL,
  loginTime timestamp(0) NOT NULL,
  expiryTime timestamp(0) NOT NULL,
  token varchar(255) NOT NULL,
  status varchar(10) DEFAULT NULL,
  activeDirectoryUser varchar(10) NOT NULL,
  user_ldap_groups varchar(2500) DEFAULT NULL,
  PRIMARY KEY (row_id),
  CONSTRAINT token UNIQUE (token)
)  ;

ALTER SEQUENCE user_token_seq RESTART WITH 15;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table validationMapping
--

DROP TABLE IF EXISTS validationMapping;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE validationMapping_seq;

CREATE TABLE validationMapping (
  id int NOT NULL DEFAULT NEXTVAL ('validationMapping_seq'),
  idApp int NOT NULL,
  relationIdApp int DEFAULT NULL,
  PRIMARY KEY (id)
) ;
