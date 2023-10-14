ALTER TABLE  `listApplications` ADD  `groupEqualityThreshold` double NULL DEFAULT  '0.0' AFTER  `groupEquality` ;
-- --------------------------------------------------------
ALTER TABLE  `listApplications` ADD  `buildHistoricFingerPrint` VARCHAR( 20 ) NULL DEFAULT  'N' AFTER  `groupEqualityThreshold` ,
ADD  `historicStartDate` DATETIME NULL AFTER  `buildHistoricFingerPrint` ,
ADD  `historicEndDate` DATETIME NULL AFTER  `historicStartDate` ,
ADD  `historicDateFormat` VARCHAR( 200 ) NULL AFTER  `historicEndDate` ;

-- Table structure for table `listStatisticalMatchingConfig`
--

CREATE TABLE IF NOT EXISTS `listStatisticalMatchingConfig` (
  `idStat` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `leftSideExp` varchar(500) NOT NULL,
  `rightSideExp` varchar(500) NOT NULL,
  `recordCountType` varchar(20) DEFAULT NULL,
  `recordCountThreshold` double DEFAULT '0',
  `measurementSum` varchar(1) DEFAULT 'N',
  `measurementSumType` varchar(20) DEFAULT NULL,
  `measurementSumThreshold` double DEFAULT '0',
  `measurementMean` varchar(1) DEFAULT 'N',
  `measurementMeanType` varchar(20) DEFAULT NULL,
  `measurementMeanThreshold` double DEFAULT '0',
  `measurementStdDev` varchar(1) DEFAULT 'N',
  `measurementStdDevType` varchar(20) DEFAULT NULL,
  `measurementStdDevThreshold` double DEFAULT '0',
  `groupBy` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`idStat`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;
