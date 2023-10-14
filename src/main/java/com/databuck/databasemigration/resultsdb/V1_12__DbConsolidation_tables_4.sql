/* --------------- Creating table for Rules -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Rules`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Rules`( 
Id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ruleName text, 
totalRecords bigint(20),  
totalFailed bigint(20), 
rulePercentage double, 
ruleThreshold double,
status text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* --------------- Creating table for Global rules-------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_GlobalRules`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_GlobalRules`( 
Id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ruleName text, 
totalRecords bigint(20),  
totalFailed bigint(20), 
rulePercentage double, 
ruleThreshold double, 
status text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* --------------- Creating table for Date Rule Summary-------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DateRule_Summary`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DateRule_Summary` (
Id BIGINT PRIMARY KEY AUTO_INCREMENT,
idApp int(11),
Date date,
Run bigint(20),
DateField text,
TotalNumberOfRecords bigint(20),
TotalFailedRecords bigint(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* --------------- Creating table for Date Rule Reference-------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DateRule_Reference`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DateRule_Reference` (
Id BIGINT PRIMARY KEY AUTO_INCREMENT,
idApp int(11),
DateField text,
dGroupVal text,
dGroupCol text,
MaxAcceptable double,
MinAcceptable double,
FailureReason text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
