/* --------------- Creating table for PatternCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Unmatched_Pattern_Data`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Unmatched_Pattern_Data`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
Col_Name text,
Total_Records bigint(20),
Total_Failed_Records bigint(20),
Pattern_Threshold double,
FailedRecords_Percentage double,
Status text
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for DefaultValueCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_default_value`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_default_value`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName nvarchar(50),
Default_Value nvarchar(50),
Default_Percentage nvarchar(50),
Default_Count nvarchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for BadDataCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_badData`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_badData`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName nvarchar(500),
TotalRecord int,
TotalBadRecord int,
badDataPercentage double,
badDataThreshold double,
status varchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for FrequencyUpdateDate -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_frequencyUpdateDate`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_frequencyUpdateDate`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
appId int(11) ,
appName text,
freqUpdateDate date,
timeSeriesType varchar(100),
colName text
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for TimelinessCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_timeliness_check`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_timeliness_check`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
SDate DATE,
EDate DATE,
TimelinessKey varchar(500),
No_Of_Days varchar(10),
Status varchar(50), 
TotalCount Long, 
TotalFailedCount Long
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
