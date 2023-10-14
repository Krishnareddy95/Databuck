ALTER TABLE  `listDataAccess` ADD  `queryString` TEXT NULL AFTER  `folderName` ;
UPDATE  `listDataAccess` SET  `queryString` = folderName;