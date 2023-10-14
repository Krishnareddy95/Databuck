ALTER TABLE  `listDataAccess` ADD  `dateFormat` VARCHAR( 50 ) DEFAULT NULL AFTER  `incrementalType` ,
ADD  `sliceStart` VARCHAR( 10 ) DEFAULT NULL AFTER  `dateFormat` ,
ADD  `sliceEnd` VARCHAR( 10 ) DEFAULT NULL AFTER  `sliceStart` 
