/* Create new table to store Rule catalog staging changes */

create table if not exists staging_listApplicationsRulesCatalog (
   row_id                  int(11) not null auto_increment primary key,
   idApp                   bigint(20) default null,
   rule_reference          int(11) default null,
   rule_code               varchar(255) default null,
   defect_code             varchar(255) default null,
   rule_type               varchar(255) not null,
   column_name             varchar(255) not null,
   rule_name 			   text,
   rule_category           varchar(255) not null,
   rule_expression         text default null,
   matching_rules           text default null,
   custom_or_global_ruleId int(11) default null,
   threshold_value         double not null default '0',
   review_comments         varchar(2000) default null,
   review_date datetime    default null,
   review_by varchar(255)  default null,
   activeFlag bit(1),
   dimension_id int(11),
   agingCheckEnabled varchar(100) default 'N' 
) engine=innodb auto_increment=1 default charset=latin1;

/* Create new table to store listApplications staging changes */

CREATE TABLE if not exists `staging_listApplications` (
  `idApp` int(11) NOT NULL,
  `name` text NOT NULL,
  `description` text,
  `appType` varchar(45) NOT NULL,
  `idData` int(20) NOT NULL,
  `idRightData` int(11) DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `updatedBy` bigint(20) DEFAULT NULL,
  `fileNameValidation` varchar(20) NOT NULL,
  `entityColumn` varchar(100) NOT NULL,
  `colOrderValidation` varchar(20) NOT NULL,
  `matchingThreshold` double DEFAULT '0',
  `nonNullCheck` varchar(45) DEFAULT NULL,
  `numericalStatCheck` varchar(10) DEFAULT NULL,
  `stringStatCheck` varchar(10) DEFAULT NULL,
  `recordAnomalyCheck` varchar(10) DEFAULT NULL,
  `incrementalMatching` varchar(10) NOT NULL DEFAULT 'N',
  `incrementalTimestamp` datetime DEFAULT NULL,
  `dataDriftCheck` varchar(10) NOT NULL DEFAULT 'N',
  `updateFrequency` varchar(100) NOT NULL DEFAULT 'Never',
  `frequencyDays` int(11) DEFAULT NULL,
  `recordCountAnomaly` varchar(10) NOT NULL DEFAULT 'N',
  `recordCountAnomalyThreshold` double NOT NULL DEFAULT '0',
  `timeSeries` varchar(500) NOT NULL DEFAULT 'None',
  `keyGroupRecordCountAnomaly` varchar(500) DEFAULT NULL,
  `outOfNormCheck` varchar(50) DEFAULT 'N',
  `applyRules` varchar(10) NOT NULL DEFAULT 'N',
  `applyDerivedColumns` varchar(10) NOT NULL DEFAULT 'N',
  `csvDir` varchar(500) DEFAULT NULL,
  `groupEquality` varchar(20) NOT NULL DEFAULT 'N',
  `groupEqualityThreshold` double DEFAULT '0',
  `buildHistoricFingerPrint` varchar(20) DEFAULT 'N',
  `historicStartDate` datetime DEFAULT NULL,
  `historicEndDate` datetime DEFAULT NULL,
  `historicDateFormat` varchar(200) DEFAULT NULL,
  `active` varchar(10) DEFAULT 'yes',
  `lengthCheck` varchar(45) DEFAULT NULL,
  `correlationcheck` varchar(10) DEFAULT 'N',
  `project_id` varchar(20) DEFAULT NULL,
  `timelinessKeyCheck` varchar(5) DEFAULT NULL,
  `defaultCheck` varchar(12) DEFAULT NULL,
  `defaultValues` varchar(12) DEFAULT NULL,
  `patternCheck` varchar(10) DEFAULT 'N',
  `dateRuleCheck` varchar(5) DEFAULT NULL,
  `badData` varchar(5) DEFAULT NULL,
  `idLeftData` int(11) DEFAULT NULL,
  `prefix1` varchar(100) DEFAULT NULL,
  `prefix2` varchar(100) DEFAULT NULL,
  `dGroupNullCheck` varchar(10) DEFAULT NULL,
  `dGroupDateRuleCheck` varchar(100) DEFAULT NULL,
  `fuzzylogic` varchar(5) DEFAULT NULL,
  `fileMonitoringType` varchar(200) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  `validityThreshold` double(40,2) DEFAULT NULL,
  `dGroupDataDriftCheck` varchar(10) DEFAULT NULL,
  `rollTargetSchemaId` int(11) DEFAULT NULL,
  `thresholdsApplyOption` int(11) NOT NULL DEFAULT '0',
  `continuousFileMonitoring` varchar(10) DEFAULT 'N',
  `rollType` varchar(50) DEFAULT NULL,
  `approve_status` int(11) DEFAULT NULL,
  `approve_comments` varchar(2000) DEFAULT NULL,
  `approve_date` datetime DEFAULT NULL,
  `approve_by` int(11) DEFAULT NULL,
  `domain_id` int(11) NOT NULL DEFAULT '0',
  `subcribed_email_id` varchar(1000) DEFAULT NULL,
  `approver_name` varchar(2500) DEFAULT NULL,
  `data_domain_id` tinyint(4) DEFAULT NULL,
  `staging_approve_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`idApp`),
  KEY `slapp_ibfk_1` (`idData`),
  CONSTRAINT `slapp_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/* Create new table to store listDFTranRule staging changes */

CREATE TABLE if not exists `staging_listDFTranRule` (
  `idDFT` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `dupRow` varchar(1) NOT NULL,
  `seqRow` varchar(1) NOT NULL,
  `seqIDcol` bigint(20) NOT NULL,
  `threshold` double NOT NULL DEFAULT '0',
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`idDFT`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=latin1;

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	start transaction;
	
	/* Add column staging_approve_status - To store approval status of staging */
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'staging_approve_status') then   
		alter table listApplications add column staging_approve_status int(11) default null;
   	end if;
   	
   	/* Add column matching_rules - To store the matching rules expression */
   	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'matching_rules') then   
		alter table listApplicationsRulesCatalog add column matching_rules text default null;
   	end if;
   	
   	/* Add column custom_or_global_ruleId - To store global rule Id from rule_template_mapping or custom rule Id from listColRules */
    if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'custom_or_global_ruleId') then   
		alter table listApplicationsRulesCatalog add custom_or_global_ruleId int(11) default null;
   	end if;	
   	
   	/* Alter the column 'rule_name' - change datatype to text */
   	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'rule_name') then   
		alter table listApplicationsRulesCatalog modify column rule_name text default null;
   	end if;
   	
   	/* Alter the column 'rule_expression' - change datatype to text */
   	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplicationsrulescatalog' and lower(column_name) = 'rule_expression') then   
		alter table listApplicationsRulesCatalog modify column rule_expression text default null;
   	end if;
   	
   	/* Create below default Dimensions if they don't exist */
   	if not exists (select 1 from dimension where dimensionName ='Validity') then   
		insert into dimension(dimensionName) values('Validity');
   	end if;
   	
   	if not exists (select 1 from dimension where dimensionName ='Completeness') then   
		insert into dimension(dimensionName) values('Completeness');
   	end if;
   	
   	if not exists (select 1 from dimension where dimensionName ='Consistency') then   
		insert into dimension(dimensionName) values('Consistency');
   	end if;
   	
   	if not exists (select 1 from dimension where dimensionName ='Accuracy') then   
		insert into dimension(dimensionName) values('Accuracy');
   	end if;
	
   	if not exists (select 1 from dimension where dimensionName ='Uniqueness') then   
		insert into dimension(dimensionName) values('Uniqueness');
   	end if;
   	
	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
 