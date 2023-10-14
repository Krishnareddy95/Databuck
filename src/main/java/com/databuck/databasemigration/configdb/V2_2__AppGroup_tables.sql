/* --------------- Creating tables for AppGroup -------------------------------- */

DROP TABLE IF EXISTS `listAppGroup`;

CREATE TABLE IF NOT EXISTS `listAppGroup` (
  `idAppGroup` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT '',
  `project_id` bigint(20) DEFAULT NULL,
  `enableScheduling` varchar(10) DEFAULT 'N',
  `idSchedule` bigint(20),
  PRIMARY KEY (`idAppGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `appGroupMapping`;

CREATE TABLE IF NOT EXISTS `appGroupMapping` (
  `idAppGroupMapping` bigint(20) NOT NULL AUTO_INCREMENT,
  `idAppGroup` bigint(20) NOT NULL,
  `idApp` bigint(20) NOT NULL,
  PRIMARY KEY (`idAppGroupMapping`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;