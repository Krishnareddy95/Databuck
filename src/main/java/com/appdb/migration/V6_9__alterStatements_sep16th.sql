ALTER TABLE  `listDataSchema` ADD  `prefixes` VARCHAR( 1000 ) NULL AFTER  `suffixes` ;
ALTER TABLE  `listDataAccess` CHANGE  `folderName`  `folderName` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ;
