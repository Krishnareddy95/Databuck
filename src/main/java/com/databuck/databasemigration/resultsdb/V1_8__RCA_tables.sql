/* --------------- Creating table for RecordCountAnomaly -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Transactionset_sum_A1`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Transactionset_sum_A1`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run int(10),
dayOfYear int(100),
month varchar(50),
dayOfMonth int(50),
dayOfWeek varchar(50),
hourOfDay int(50),
RecordCount bigint,
fileNameValidationStatus varchar(20),
columnOrderValidationStatus varchar(20),
DuplicateDataSet varchar(50),
RC_Std_Dev  double,
RC_Mean double,
RC_Deviation double,
RC_Std_Dev_Status varchar(50),
RC_Mean_Moving_Avg varchar(50),
RC_Mean_Moving_Avg_Status varchar(50),
measurementCol varchar(2000),
SumOf_M_Col Double,
M_Std_Dev double,
M_Mean double,
M_Deviation double,
M_Std_Dev_Status varchar(50),
M_Mean_Moving_Avg varchar(50),
M_Mean_Moving_Avg_Status varchar(50),
recordAnomalyCount bigint,
dGroupVal text,
dGroupCol text,
missingDates text
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/* --------------- Creating table for MicrosegmentBasedRecordCountAnomaly -------------------------------- */

DROP TABLE IF EXISTS `DATA_QUALITY_Transactionset_sum_dgroup`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Transactionset_sum_dgroup` ( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11),
Date date,
Run int(10),
dayOfYear int(100),
month varchar(50),
dayOfMonth int(50),
dayOfWeek varchar(50),
hourOfDay int(50),
RecordCount bigint,
RC_Std_Dev  double,
RC_Mean double,
dGroupDeviation double,
dGroupRcStatus varchar(50),
measurementCol varchar(2000),
SumOf_M_Col Double,
M_Std_Dev double,
M_Mean double,
M_Deviation double,
M_Std_Dev_Status varchar(50),
recordAnomalyCount bigint,
dGroupVal text,
dGroupCol text,
missingDates text,
fileNameValidationStatus varchar(20),
columnOrderValidationStatus varchar(20),
DuplicateDataSet varchar(50),
Action varchar(100),
UserName varchar(100),
Time varchar(100),
Validity varchar(100)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
