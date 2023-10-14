CREATE TABLE `list_batch_schema` (
`idBatchSchema` bigint(20) NOT NULL AUTO_INCREMENT,
  `schemaBatchName` text NOT NULL,
  `schemaBatchType` text NOT NULL,
  `batchFileLocation` text NOT NULL,
  `totalSchemas` BIGINT NULL,
  `completedSchemas` BIGINT NULL,
  `idDataSchemas` text NULL,
  PRIMARY KEY (`idBatchSchema`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE  `listApplications` CHANGE  `name`  `name` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ;
ALTER TABLE  `listApplications` CHANGE  `description`  `description` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ;
