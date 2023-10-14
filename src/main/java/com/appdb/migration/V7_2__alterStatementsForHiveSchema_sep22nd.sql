ALTER TABLE  `hiveSource` ADD  `totalTables` BIGINT NULL AFTER  `recordCount` ,
ADD  `completedTables` BIGINT NULL AFTER  `totalTables` ;

ALTER TABLE  `hiveSource` ADD  `columnType` TEXT NULL AFTER  `columnName` ;