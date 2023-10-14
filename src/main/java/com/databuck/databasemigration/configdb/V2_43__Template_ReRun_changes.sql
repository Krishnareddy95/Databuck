/* --------------- Creating table for staging of ListDataDefinition -------------------------------- */
DROP TABLE IF EXISTS `staging_listDataDefinition`;

CREATE TABLE IF NOT EXISTS `staging_listDataDefinition`( 
  `idColumn` bigint(20) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `columnName` varchar(45) NOT NULL,
  `displayName` varchar(200) NOT NULL,
  `format` varchar(45) DEFAULT NULL,
  `hashValue` varchar(1) NOT NULL DEFAULT 'N',
  `numericalStat` varchar(1) NOT NULL DEFAULT 'N',
  `stringStat` varchar(1) NOT NULL DEFAULT 'N',
  `nullCountThreshold` double DEFAULT '0',
  `numericalThreshold` double DEFAULT '0',
  `stringStatThreshold` double DEFAULT '0',
  `KBE` varchar(1) DEFAULT 'N',
  `dgroup` varchar(1) DEFAULT 'N',
  `dupkey` varchar(1) DEFAULT 'N',
  `measurement` varchar(1) DEFAULT 'N',
  `blend` varchar(1) NOT NULL DEFAULT 'N',
  `idCol` int(11) DEFAULT NULL,
  `incrementalCol` varchar(10) NOT NULL DEFAULT 'N',
  `idDataSchema` bigint(20) NOT NULL DEFAULT '0',
  `nonNull` varchar(20) NOT NULL DEFAULT 'N',
  `primaryKey` varchar(20) NOT NULL DEFAULT 'N',
  `recordAnomaly` varchar(20) NOT NULL DEFAULT 'N',
  `recordAnomalyThreshold` double DEFAULT '0',
  `dataDrift` varchar(10) NOT NULL DEFAULT 'N',
  `dataDriftThreshold` double NOT NULL DEFAULT '0',
  `outOfNormStat` varchar(50) DEFAULT 'N',
  `outOfNormStatThreshold` double NOT NULL DEFAULT '0',
  `isMasked` varchar(10) DEFAULT NULL,
  `partitionBy` varchar(10) DEFAULT 'N',
  `lengthCheck` varchar(45) DEFAULT NULL,
  `lengthValue` varchar(100) DEFAULT NULL,
  `applyrule` varchar(45) DEFAULT 'N',
  `correlationcolumn` varchar(10) DEFAULT 'N',
  `startDate` varchar(5) DEFAULT NULL,
  `timelinessKey` varchar(5) DEFAULT NULL,
  `endDate` varchar(5) DEFAULT NULL,
  `defaultCheck` varchar(12) DEFAULT NULL,
  `defaultValues` varchar(12) DEFAULT NULL,
  `patternCheck` varchar(10) DEFAULT 'N',
  `patterns` varchar(500) DEFAULT NULL,
  `badData` varchar(5) DEFAULT 'N',
  `dateRule` varchar(5) DEFAULT 'N',
  `dateFormat` varchar(50) DEFAULT NULL,
  `lengthCheckThreshold` double DEFAULT '0',
  `badDataCheckThreshold` double DEFAULT '0',
  `patternCheckThreshold` double DEFAULT '0',
  PRIMARY KEY (`idColumn`)
) ENGINE=InnoDB AUTO_INCREMENT=6745 DEFAULT CHARSET=latin1;

/* --------------- Adding new columns to listDataSources - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* Add template_create_success column to listDataSources */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdatasources' and lower(COLUMN_NAME) = 'template_create_success') then	
		alter table listDataSources
			add column template_create_success varchar(20) DEFAULT 'N';
	end if;
	
	/* Add deltaApprovalStatus column to listDataSources */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdatasources' and lower(COLUMN_NAME) = 'deltaapprovalstatus') then	
		alter table listDataSources
			add column deltaApprovalStatus varchar(2000) DEFAULT null;
	end if;
	
	/* Drop profilingReRunEnabled column from listDataSources */
	if exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listdatasources' and lower(COLUMN_NAME) = 'profilingrerunenabled') then	
		alter table listDataSources drop column profilingReRunEnabled;
	end if;
	
	/* Add Date column to listAdvancedRules */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listadvancedrules' and lower(COLUMN_NAME) = 'date') then	
		alter table listAdvancedRules add column Date date;
	end if;
	
	/* Add Run column to listAdvancedRules */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'listadvancedrules' and lower(COLUMN_NAME) = 'run') then	
		alter table listAdvancedRules add column Run bigint(20);
	end if;
	
	/* Add uniqueId column to runTemplateTasks */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'runtemplatetasks' and lower(COLUMN_NAME) = 'uniqueid') then	
		alter table runTemplateTasks
			add column uniqueId varchar(2000) DEFAULT null;
	end if;
	
	/* Add templateRunType column to runTemplateTasks */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'runtemplatetasks' and lower(COLUMN_NAME) = 'templateruntype') then	
		alter table runTemplateTasks
			add column templateRunType varchar(2000) DEFAULT 'newtemplate';
	end if;
	
	start transaction;
	
	/* For all the existing active templates if listDataDefinition count > 0 and listDataAccess = 1 set template_create_success='Y' */
	update listDataSources d set d.template_create_success='Y' where d.active='yes' and d.dataLocation not in ('File Management','Kafka') and (select count(*) from listDataDefinition l where l.idData=d.idData) > 0 and (select count(*) from listDataAccess la where la.idData=d.idData) = 1;

	/* Update Date and Run for all template ids in listAdvancedRules */
	update listAdvancedRules d set d.Date=(select case when (t.startTime is not null) then date_format(t.startTime,'%Y-%m-%d') else CURDATE() end as date from runTemplateTasks t where t.idData=d.idData order by t.startTime desc limit 1), d.Run=1 ;
	update listAdvancedRules set Date=CURDATE(), Run=1 where Date is null;

	/* Delete all the entries from runTemplateTasks */
	delete from runTemplateTasks;
	
	commit;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;