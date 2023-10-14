ALTER TABLE  `hiveSource` ADD  `columnName` TEXT NULL AFTER  `tableName` ,
ADD  `recordCount` BIGINT NULL AFTER  `columnName` ;