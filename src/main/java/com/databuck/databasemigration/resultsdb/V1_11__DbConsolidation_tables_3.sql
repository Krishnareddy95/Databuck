/* --------------- Creating table for NullCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_NullCheck_Summary`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_NullCheck_Summary`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName text,
Record_Count bigint,
Null_Value bigint,
Null_Percentage double,
Null_Threshold double,
Status varchar(20),
Historic_Null_Mean double,
Historic_Null_stddev double,
Historic_Null_Status varchar(20)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/* --------------- Creating table for RecordAnomaly -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Record_Anomaly`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Record_Anomaly`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName text,
ColVal double,
mean double,
stddev double,
dGroupVal text,
dGroupCol text,
ra_Deviation double,
Status text,
RA_Dqi double
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/* --------------- Creating table for History RecordAnomaly -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_History_Anomaly`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_History_Anomaly`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName text,
ColVal int(50),
mean double,
stddev double,
dGroupVal nvarchar(100),
dGroupCol nvarchar(100),
ra_Deviation double,
RA_Dqi double,
status nvarchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for DistributionCheck(Numericalstat) -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Column_Summary`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Column_Summary`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
dayOfYear int(100),
month varchar(50),
dayOfMonth int(50),
dayOfWeek varchar(50),
hourOfDay int(50),
ColName varchar(200),
Count bigint,
Min double,
Max double,
Cardinality int(50),
Std_Dev double,
Mean double,
Null_Value bigint,
Status varchar(20),
StringCardinalityAvg double,
StringCardinalityStdDev double,
StrCardinalityDeviation double,
String_Threshold double,
String_Status varchar(50),
NumMeanAvg double,
NumMeanStdDev double,
NumMeanDeviation double,
NumMeanThreshold double,
NumMeanStatus varchar(50),
NumSDAvg double,
NumSDStdDev double,
NumSDDeviation double,
NumSDThreshold double,
NumSDStatus varchar(50),
outOfNormStatStatus varchar(100),
sumOfNumStat double,
NumSumAvg double,
NumSumStdDev double,
NumSumThreshold double,
dGroupVal text,
dGroupCol text,
dataDriftCount bigint(200),
dataDriftStatus varchar(50),
Default_Value bigint,
Default_Count bigint,
Record_Count bigint,
Null_Percentage double,
Default_Percentage double,
Null_Threshold double,
Default_Threshold double
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for DataDrift -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DATA_DRIFT`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DATA_DRIFT`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
colName text,
uniqueValues text,
potentialDuplicates text,
dGroupVal nvarchar(1000),
dGroupCol nvarchar(1000)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for DataDrift Summary -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DATA_DRIFT_SUMMARY`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DATA_DRIFT_SUMMARY`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
colName text,
uniqueValues text,
Operation text,
dGroupVal nvarchar(1000),
dGroupCol nvarchar(1000),
userName text,
Time text,
Status varchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for DataDrift Count Summary -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
colName text,
uniqueValuesCount int(10),
missingValueCount int(10),
newValueCount int(10)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
