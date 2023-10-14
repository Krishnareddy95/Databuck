ALTER TABLE  `listDataSources` ADD  `active` VARCHAR( 10 ) NULL DEFAULT  'yes' AFTER  `ignoreRowsCount` ;
-- --------------------------------------------------------
ALTER TABLE  `listApplications` ADD  `active` VARCHAR( 10 ) NULL DEFAULT  'yes' AFTER  `historicDateFormat` ;

-- Table structure for table `listModelGovernance`
--

CREATE TABLE IF NOT EXISTS `listModelGovernance` (
  `idModel` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `modelGovernanceType` varchar(50) DEFAULT NULL,
  `modelIdCol` varchar(50) DEFAULT NULL,
  `decileCol` varchar(50) DEFAULT NULL,
  `expectedPercentage` double NOT NULL DEFAULT '0',
  `thresholdPercentage` double NOT NULL DEFAULT '0',
  `leftSourceSliceStart` varchar(50) DEFAULT NULL,
  `leftSourceSliceEnd` varchar(50) DEFAULT NULL,
  `rightSourceSliceStart` varchar(50) DEFAULT NULL,
  `rightSourceSliceEnd` varchar(50) DEFAULT NULL,
  `measurementExpression` varchar(1000) DEFAULT NULL,
  `matchingExpression` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idModel`)
)ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;
