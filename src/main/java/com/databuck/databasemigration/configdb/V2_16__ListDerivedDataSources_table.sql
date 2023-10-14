
-- Table structure for table `listDerivedDataSources` --
DROP TABLE IF EXISTS `listDerivedDataSources`;

CREATE TABLE IF NOT EXISTS `listDerivedDataSources` (
  `idDerivedData` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `name` varchar(1000) NOT NULL DEFAULT '',
  `description` varchar(1000) NOT NULL,
  `template1Name` varchar(1000) DEFAULT NULL,
  `template1IdData` int(11) DEFAULT NULL,
  `template1AliasName` varchar(200) DEFAULT NULL,
  `template2Name` varchar(1000) DEFAULT NULL,
  `template2IdData` int(11) DEFAULT NULL,
  `template2AliasName` varchar(200) DEFAULT NULL,
  `queryText` varchar(10000) DEFAULT NULL,
  `createdBy` int(11) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `updatedBy` bigint(20) DEFAULT NULL,
  `project_id` int(20) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idDerivedData`),
  KEY `listDerivedDataSources_idfk_1` (`idData`),
  CONSTRAINT `listDerivedDataSources_idfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
