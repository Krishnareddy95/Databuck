/* --------------- Creating table for Roll Data Matching -------------------------------- */
DROP TABLE IF EXISTS `roll_data_matching_dashboard`;

CREATE TABLE IF NOT EXISTS `roll_data_matching_dashboard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idapp` bigint(20) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `run` bigint(20) DEFAULT NULL,
  `validationCheckName` text,
  `source1Name` text,
  `source2Name` text,
  `source1Count` bigint(20) DEFAULT NULL,
  `source1OnlyRecords` bigint(20) DEFAULT NULL,
  `source1Status` text,
  `source2Count` bigint(20) DEFAULT NULL,
  `source2OnlyRecords` bigint(20) DEFAULT NULL,
  `source2Status` text,
  `unMatchedRecords` bigint(20) DEFAULT NULL,
  `unMatchedStatus` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;