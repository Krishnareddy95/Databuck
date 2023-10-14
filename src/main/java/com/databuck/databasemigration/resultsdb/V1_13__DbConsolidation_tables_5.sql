/* --------------- Creating table for Duplicate Primary keys -------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Transaction_Detail_Identity`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Transaction_Detail_Identity` (
Id BIGINT PRIMARY KEY AUTO_INCREMENT,
idApp int(11),
Date date,
Run bigint(20),
duplicateCheckFields text,
duplicateCheckValues text,
dupcount  bigint(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* --------------- Creating table for Duplicate all-------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_Transaction_Detail_All`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_Transaction_Detail_All` (
Id BIGINT PRIMARY KEY AUTO_INCREMENT,
idApp int(11),
Date date,
Run bigint(20),
duplicateCheckFields text,
duplicateCheckValues text,
dupcount  bigint(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* --------------- Creating table for DateRule check failed records-------------------------------- */
DROP TABLE IF EXISTS `DATA_QUALITY_DateRule_FailedRecords`;

CREATE TABLE IF NOT EXISTS `DATA_QUALITY_DateRule_FailedRecords` (
Id BIGINT PRIMARY KEY AUTO_INCREMENT,
idApp int(11),
Date date,
Run bigint(20),
DateFieldCols text,
DateFieldValues text,
dGroupVal text,
dGroupCol text,
FailureReason  text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;