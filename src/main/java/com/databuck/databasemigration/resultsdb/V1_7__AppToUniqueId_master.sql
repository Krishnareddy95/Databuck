/* --------------- Creating table for App to UniqueId Run details -------------------------------- */
DROP TABLE IF EXISTS `app_uniqueId_master_table`;

CREATE TABLE IF NOT EXISTS `app_uniqueId_master_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(1000) NOT NULL UNIQUE,
  `idapp` bigint(20) NOT NULL,
  `execution_date` date DEFAULT NULL,
  `run` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;