ALTER TABLE  `File_Management_Run` ADD  `realFileName` VARCHAR( 100 ) NULL AFTER  `extraFiles` ,
ADD  `lastProcessedTimestamp` VARCHAR( 200 ) NULL AFTER  `realFileName` ;