/* --------------- Creating table for LengthCheck -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Length_Check`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Length_Check`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
ColName text,
Length text,
TotalFailedRecords double,
Length_Threshold double,
RecordCount int(11),
FailedRecords_Percentage double,
Status varchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


/* --------------- Creating table for Duplicate check - Transaction summary -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Transaction_Summary`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Transaction_Summary`( 
Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
idApp int(11) ,
Date date,
Run bigint(20),
Duplicate bigint(20),
Type varchar(50),
TotalCount bigint(20),
Percentage double,
Threshold double,
Status varchar(50)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
