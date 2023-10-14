CREATE TABLE IF NOT EXISTS  `data_matching_dashboard` (
`id` BIGINT( 20 ) NOT NULL AUTO_INCREMENT ,
`idapp` BIGINT( 20 ) NOT NULL ,
 `date` DATE DEFAULT NULL ,
`run` BIGINT( 20 ) DEFAULT NULL ,
`validationCheckName` TEXT,
`source1Name` TEXT,
 `source2Name` TEXT,
`source1Count` BIGINT( 20 ) DEFAULT NULL ,
`source1OnlyRecords` BIGINT( 20 ) DEFAULT NULL ,
 `source1Status` TEXT,
`source2Count` BIGINT( 20 ) DEFAULT NULL ,
`source2OnlyRecords` BIGINT( 20 ) DEFAULT NULL ,
 `source2Status` TEXT,
`unMatchedRecords` BIGINT( 20 ) DEFAULT NULL ,
`unMatchedStatus` TEXT,
PRIMARY KEY (  `id` )
) ENGINE = INNODB DEFAULT CHARSET = latin1;
					
					
CREATE TABLE IF NOT EXISTS  `data_quality_dashboard` (
`id` BIGINT( 20 ) NOT NULL AUTO_INCREMENT ,
`IdApp` BIGINT( 20 ) NOT NULL ,
`date` DATE DEFAULT NULL ,
 `run` BIGINT( 20 ) DEFAULT NULL ,
`validationCheckName` TEXT,
`sourceName` TEXT,
`recordCountStatus` TEXT,
 `nullCountStatus` TEXT,
`primaryKeyStatus` TEXT,
`userSelectedFieldStatus` TEXT,
`numericalFieldStatus` TEXT,
 `stringFieldStatus` TEXT,
`recordAnomalyStatus` TEXT,
`dataDriftStatus` TEXT,
PRIMARY KEY (  `id` )
) ENGINE = INNODB DEFAULT CHARSET = latin1	