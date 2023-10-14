package com.databuck.constants;

import java.util.Arrays;
import java.util.List;

public class DatabuckConstants {
	public static final String RULE_CATALOG_CREATE_STATUS = "CREATED";
	public static final String RULE_CATALOG_RUNNABLE_STATUS_1 = "UNIT_TEST_READY";
	public static final String RULE_CATALOG_RUNNABLE_STATUS_2 = "APPROVED_FOR_PRODUCTION";
	public static final List<String> KMS_ENABLED_SCHEMA_TYPES = Arrays.asList("Oracle", "MSSQL", "Teradata");

	/*
	 * Constants for different entities from where details of checks enabled on a
	 * validation is read to create or update the Rule Catalog
	 */
	public static final String RC_ENTITY_LISTAPPLICATIONS = "listApplications";
	public static final String RC_ENTITY_LISTDFTRANRULE = "listDFTranRule";

	/* Constants for different rule Categories in Rule Catalog */
	public static final String RULE_CATEGORY_AUTO_DISCOVERY = "Auto Discovery";
	public static final String RULE_CATEGORY_CUSTOM = "Custom";

	/* Constants for different Check Types in Rule Catalog */
	public static final String RC_CHECK_TYPE_COLUMN = "Column";
	public static final String RC_CHECK_TYPE_RULES = "Rules";

	/* Constants for different rule types in Rule Catalog */
	public static final String RULE_TYPE_RULES = "Rules";
	public static final String RULE_TYPE_GLOBAL_RULE = "Global Rule";
	public static final String RULE_TYPE_CUSTOM_RULE = "Custom Rule";
	public static final String RULE_TYPE_NULLCHECK = "Null Check";
	public static final String RULE_TYPE_LENGTHCHECK = "Length Check";
	public static final String RULE_TYPE_MAXLENGTHCHECK = "Max Length Check";
	public static final String RULE_TYPE_BADDATACHECK = "Bad Data Check";
	public static final String RULE_TYPE_DEFAULTCHECK = "Default Check";
	public static final String RULE_TYPE_PATTERNCHECK = "Pattern Check";
	public static final String RULE_TYPE_DEFAULTPATTERNCHECK = "Default Pattern Check";
	public static final String RULE_TYPE_DATERULECHECK = "Date Rule Check";
	public static final String RULE_TYPE_DATADRIFTCHECK = "Data Drift Check";
	public static final String RULE_TYPE_VALUEANOMALYCHECK = "Value Anomaly Check";
	public static final String RULE_TYPE_TIMELINESSCHECK = "Timeliness Check";

	public static final String RULE_TYPE_NUMERICALSTATISTICSCHECK = "Numerical Statistics Check";
	// public static final String RULE_TYPE_NUMERICALSTATISTICSCHECK = "Distribution
	// Check";
	public static final String RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS = "Duplicate Check PrimaryFields";
	public static final String RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS = "Duplicate Check SelectedFields";
	public static final String RULE_TYPE_RECORDCOUNTANOMALY = "Record Count Anomaly";
	public static final String RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY = "Microsegment Record Count Anomaly";

	/* Constants for default dimension names */
	public static final String DIMENSION_VALIDITY = "Validity";
	public static final String DIMENSION_COMPLETENESS = "Completeness";
	public static final String DIMENSION_CONSISTENCY = "Consistency";
	public static final String DIMENSION_ACCURACY = "Accuracy";
	public static final String DIMENSION_UNIQUENESS = "Uniqueness";

	/* Constants for defining key and values for validation run type */
	public static final String VAL_RUN_TYPE_FULL_LOAD = "full_load";
	public static final String VAL_RUN_TYPE_UNIT_TESTING = "unit_testing";
	public static final String VAL_RUN_TYPE_FULL_LOAD_DISPLAY_NAME = "Full Load";
	public static final String VAL_RUN_TYPE_UNIT_TESTING_DISPLAY_NAME = "Unit Testing";

	/* Constants for Time zone short IDs */
	public static final String DATABUCK_JOB_TIMEZONE_UTC = "UTC";

	/* Constants for rules types */
	public static final String REFERENTIAL_RULE = "referential";
	public static final String ORPHAN_RULE = "orphan";
	public static final String CROSSREFERENTIAL_RULE = "cross referential";
	public static final String CONDITIONAL_CROSSREFERENTIAL_RULE = "conditional cross referential";
	public static final String CONDITIONAL_ORPHAN_RULE = "conditional orphan";
	public static final String CONDITIONAL_REFERENTIAL_RULE = "conditional referential";
	public static final String CONDITIONAL_SQLINTERNAL_RULE = "conditional sql internal rule";
	public static final String SQL_INTERNAL_RULE = "sql internal rule";
	public static final String CONDITIONAL_DUPLICATE_CHECK = "conditional duplicate check";
	public static final String CONDITIONAL_COMPLETENESS_CHECK = "conditional completeness check";
	public static final String DIRECT_QUERY_RULE = "direct query";

	/* Constant for FILE MONITORING */
	public static final String FILE_ARRIVAL_STATUS_ON_TIME = "on time";
	public static final String FILE_ARRIVAL_STATUS_EARLY = "early";
	public static final String FILE_ARRIVAL_STATUS_DELAYED = "delayed";
	public static final String FILE_ARRIVAL_MISSING = "missing";
	public static final String FILE_ARRIVAL_NEW_FILE = "new file";
	public static final String FILE_ARRIVAL_ADDITIONAL = "additional";
	public static final String FILE_INDICATOR_FRQUENCY = "frequency";
	public static final String FILE_INDICATOR_HOURLY = "hourly";
	public static final String FM_ACTIVITY_MONITORING = "FM_MONITORING";
	public static final String FM_ACTIVITY_ANALYSIS = "FM_ANALYSIS";
	public static final String FM_ACTIVITY_PROCESSING = "FM_PROCESSING";
	public static final String FM_AUDIT_STATUS_COMPLETED = "completed";
	public static final String FM_AUDIT_STATUS_IN_PROGRESS = "in progress";
	public static final String FM_AUDIT_STATUS_FAILED = "failed";
	public static final String FM_TOPIC_FOR_ALERT = "FILE_MONITORING_MISSING_FILE_EVENT";

	/* Constants for Remote Cluster API */
	public static final String URI_PREFIX = "https://";
	public static final String API_CONTEXT = "/databuck/proxy/";
	public static final String DEAFAULT_CLUSTER_CATEGORY = "cluster";
	public static final String DATA_CONNECTION_API = "testDataConnection";
	public static final String TOKEN_GENERATE_API = "generateAPIToken";
	public static final String KILL_JOB_API = "killTask";
	public static final String RUN_TASK_API = "runTask";
	public static final String GET_METADATA_API = "getTableMetadata";
	public static final String GET_TABLESLIST_API = "getTablesList";
	public static final String GET_PROFILEDATA_API = "getProfileData";

	public static final String API_PARAM_DOMAIN_NAME = "domainName";
	public static final String API_PARAM_MAPR_TICKET = "mapr.ticket.enabled";
	public static final String API_PARAM_TASK_NAME = "taskName";
	public static final String API_PARAM_DEPLOY_MODE = "deployMode";
	public static final String API_PARAM_SPARK_ID = "sparkAppId";
	public static final String API_PARAM_PROCESS_ID = "processId";
	public static final String API_PARAM_APPLICATION_ID = "applicationId";
	public static final String API_PARAM_REQUEST_TYPE = "requestType";

	/*
	 * Application supported DB types
	 */
	public static final String DB_TYPE_MYSQL = "mysql";
	public static final String DB_TYPE_POSTGRES = "postgres";
	public static final String EVERYDAY_AT_ELEVEN_CRON = "0 0 11 * * ?";
	public static final String CROSSORIGIN = "*";

	/* Constants for message subject fillers */
	public static final String USER_FILLER = "{{User}}";
	public static final String NAME_FILLER = "{Name}";
	public static final String FOCUS_OBJECT_ID_FILLER = "{FocusObjectId}";
	public static final String DATABUCK_BASE_URL_FILLER = "{databuckBaseUrl}";
	public static final String DQI_FILLER = "{dqi}";
	public static final String STATUS_FILLER = "{status}";
	public static final String LIECENSE_DECRYPTOR_PASSWORD = "4qsE9gaz%!L@UMrK5myY";

	public static final String ACTIVITY_TYPE_CREATED="Created";

	public static final String ACTIVITY_TYPE_EDITED="Edited";
	public static final String ACTIVITY_TYPE_DELETED="Deleted";
	public static final String ACTIVITY_TYPE_VIEWED="Viewed";
	public static final String DBK_FEATURE_TEMPLATE="Template";
	public static final String DBK_FEATURE_DERIVED_TEMPLATE="Derived Template";
	public static final String DBK_FEATURE_CONNECTION="Connection";
	public static final String DBK_FEATURE_VALIDATION="Validation";
	public static final String DBK_FEATURE_SYNONYM="Synonym";
	public static final String DBK_FEATURE_GLOBAL_RULE="CustomRule";
	public static final String DBK_FEATURE_GLOBAL_FILTER="GlobalFilter";
	public static final String DBK_FEATURE_INTERNAL_REFERENCES="InternalReferences";
	public static final String DBK_FEATURE_INTERNAL_REFERENCESVIEW="ReferencesView";
	public static final String DBK_FEATURE_USER ="Users";
	public static final String DBK_FEATURE_ROLE ="Roles";
	public static final String DBK_FEATURE_PROJECT ="Projects";
	public static final String DBK_FEATURE_DOMAIN ="Domain";
	public static final String DBK_FEATURE_DIMENSION ="Dimension";
	public static final String DBK_FEATURE_DEFECTCODES ="DefectCodes";
	public static final String DBK_FEATURE_DATABUCKTAGS ="DatabuckTags";
	public static final String DBK_FEATURE_APPLICATION_SETTINGS ="ApplicationSettings";
	public static final String DBK_FEATURE_ALERT_NOTIFICATIONS ="AlertNotifications";
	public static final String DBK_FEATURE_ALERT_EVENT_SUBSCRIPTION ="AlertEventSubscription";
	public static final String DBK_FEATURE_APP_GROUP ="AppGroup";
	public static final String DBK_FEATURE_GENERATE_API_TOKEN ="GenerateApiToken";
	public static final String DBK_FEATURE_SCHEDULER ="Scheduler";
	public static final String DBK_FEATURE_QUICK_START ="Quick Start";
	public static final String DBK_FEATURE_TRIGGER ="Trigger";
	public static final String DEFAULT_VALIDATION_JOB_SIZE ="low";

	public static final String DBK_FEATURE_ALERT_INBOX ="Alert Inbox ";
	public static final String DBK_FEATURE_CUSTOM_MICROSEGMENTS ="Custom Microsegments ";
	public static final String DBK_FEATURE_LOGIN_GROUP_MAPPING ="Login Group Mapping";
	public static final String DBK_FEATURE_IMPORT = "Import";
	public static final String DBK_FEATURE_EXPORT ="Export";
	public static final String DBK_FEATURE_STATUS_FAILED ="Failed";
	public static final String DBK_FEATURE_STATUS_INPROGRESS ="In progress";
	public static final String DBK_FEATURE_STATUS_SUCCESS ="Success";

}
