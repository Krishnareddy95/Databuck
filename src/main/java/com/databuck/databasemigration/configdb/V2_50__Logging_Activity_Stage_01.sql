/* 
	5-Feb-2021 : create logging_activity and databuck_activity_urls for user activity logs with databuck_activity_urls insert statements
*/
create table if not exists logging_activity (
	row_id					int(11) not null auto_increment primary key,
	user_id					int(11) not null,
	user_name				varchar(15) not null,
	access_url				varchar(50) not null,
	databuck_feature		varchar(255) null,
	session_id				varchar(255) null,
	activity_log_time		varchar(40) not null	
) engine=innodb auto_increment=1 default charset=latin1;

create table if not exists databuck_activity_urls (
   row_id                  int(11) not null auto_increment primary key,
   activity_title          varchar(255),
   http_url                varchar(255) default null,
   unique key unique_http_url (http_url)
) engine=innodb auto_increment=1 default charset=latin1;

/* delete all previous data so all environment get all data properly via this correcive script */
truncate table logging_activity;
truncate table databuck_activity_urls;

drop procedure if exists dummy_do_not_use;

start transaction;

INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('DataConnection View','/dataConnectionView');
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Get Application Ids','/validationIdApp');
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('User Activity Log','/accessLog');
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('DataConnection Edit','/editSchema');
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('DataConnection Delete','/deleteSchema');
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New DataConnection' ,'/createSchema'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Add New Batch DataConnection' , '/addNewBatch'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Data Template'  ,'/datatemplateview'           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'List Template Columns', '/listdataview'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Data Template Edit' , '/editDataTemplate'           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Data Template  Delete' , '/deletedatasource'                        );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Add New Data Template', '/dataTemplateAddNew'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Derived Template' , '/derivedTemplateAddNew'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'View Extend Template' , '/extendTemplateView'                   );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Extend Template' , '/index'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add Derived Column'  ,	'/createcolumn'                                   );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Delete Extended Template'  ,'/deleteTemp'                            );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Add New Extend Template' ,'/addNewExtendTemplate'                              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Rules','/viewRules'                    );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Edit Rules'  ,'/editExtendTemplateRule'                               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Rules'  ,'/addNewRule'                  );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('View Advance Rules' ,'/advancedRulesTemplateView'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Global Rules' ,'/viewGlobalRules'                   );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Edit Global Rules' , '/editGlobalRule'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Rule Global' ,'/AddNewRuleGlobal'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Synonyms Library','/viewsynonyms'                        );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'References View','/refdatatemplateview'                            );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Reference','/refData'                     );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add Internal References' , '/addReferences'                                 );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add Global Thresholds', '/globalThreshold'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Validation', '/validationCheck_View'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Rules Catalog' , '/getRuleCatalog'                    );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Customize Validation Check' ,'/customizeValidation'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'ValidationCheck Source' ,'/dataSourceDisplayAllView'                     );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Create Validation' ,'/dataApplicationCreateView'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Submit FileMonitoring'  , '/submitFileMonitoringCSV'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Add Batch Validation'  ,'/batchValidation'                 );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Run Validation or AppGroup' ,'/listApplicationsView'                    );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Run Schema'  ,'/runAppGroup'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Job Status','/runningJobsView'                               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Schedules' ,'/viewSchedules'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Edit Schedule' ,'/editSchedule'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Delete Schedule' ,'/deleteSchedule'                            );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Schedules' ,'/scheduledTask'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Triggers' ,'/viewTriggers'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Delete Trigger' ,'/deleteTrigger'                            );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Add New Triggers' ,'/triggerTask'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View App Groups' ,'/viewAppGroups'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Customize AppGroups' ,'/customizeAppGroup'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New App Groups' ,'/addAppGroup'                       );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Data Quality view' ,'/dashboard_View'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Count reasonability','/dashboard_table'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Approval process' ,'/reviewProcessController'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Microsegment Validity','/validity'                                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Duplicate check' ,'/dupstats'                                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Data Completeness' ,'/nullstats'                                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Conformity'  ,'/badData'                               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Custom Rules','/sqlRules'                                 );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Data Drift' ,'/stringstats'                                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Distribution Check','/numericstats'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Record Anomaly','/recordAnomaly'                            );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Sequence','/timelinessCheck'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Exceptions','/exceptions'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'RootCause Analysis' ,'/rootCauseAnalysis'                              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('DT Profile view','/profileDataTemplateView'                       );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Profiling result view' ,'/dataProfiling_View'                 );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Key Measurement Matching','/getDataMatchingResults'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Roll DataMatching' ,'/getRollDataMatchingResults'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('view Roll DataMatching results' ,'/getRollDataMatchTablesData'              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('FingerPrint Matching' ,'/StatisticalMatchingResultView'              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Schema Matching' ,'/SchemaMatchingResultView'           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('show schema matching', '/showSchemaMatchingData'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'File Monitoring view'  ,'/fileMonitoringView'                  );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'file Monitoring Results','/fileMonitorResults'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'File Management' ,'/FileManagementResultView'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Model Governance','/ModelGovernanceResultView'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Model Governance Dashboard view' ,'/ModelGovernanceDashboardResultView'               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Log Files','/downloadLogFiles'     					 );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Dash Configuration','/dashConfiguration'                        );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Change Password' , '/changePassword'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Generate API Token' ,  '/generateSecureAPI'                       );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'migrate Database'  ,  '/migrateDatabase'                         );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'View Modules' , '/accessControls'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Roles' ,  '/roleManagement'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Edit Role' , '/editRoleModule'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Role' ,'/addNewRole'                              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'View Users' ,  '/viewUsers'                               );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New User'  ,  '/addNewUser'                              );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Domain Library' , '/domainViewList'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Project' , '/viewProject'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Edit Project' ,   '/editProject'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Delete Project'   , '/deleteProject'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (   'Add New Project' ,   '/addNewProject'                           );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Map ADGroup & Role' ,  '/groupRoleMapAddnew'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Add New Location' ,  '/addNewLocation'                          );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (   'Map Location & Validation' ,   '/mapLocationAndValidation'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'License Information' ,  '/licenseInformation'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Import'   ,   '/getImportUI'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Submit Import Form' ,   '/submitImportUiForm'                      );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Export'  ,   '/getExportUI'                             );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES (  'Export Connection View' ,  '/exportDataConnectionView'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Export CSV File',  '/exportCSVFileData'                       );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Export Template View'  ,   '/exportDataTemplateView'                  );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Export ValidationCheck View' ,   '/exportValidationCheck'                   );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ('Features Access Control' ,   '/databuck_activity_urlsAccessControl'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'View Application Settings'  ,	'/applicationSettingsView'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Exception Data Report'  ,	'/ManageExceptionDataReport'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Notification View'  ,	'/notificationView'                );
INSERT INTO databuck_activity_urls (activity_title,http_url) VALUES ( 'Login Group Mapping'  ,	'/loginGroupMapping'                );

commit;