ALTER TABLE `sub_task_status`
 DROP `regex`,
  DROP `orphan`,
  DROP `referential`,
  DROP `crossreferential`;
ALTER TABLE `sub_task_status` ADD `rules` VARCHAR( 10 ) NULL DEFAULT NULL AFTER `datadrift` ;

ALTER TABLE  `task_progress_status` ADD  `dfread2` VARCHAR( 1000 ) NULL AFTER  `dfread` ,
ADD  `matchingStatus` VARCHAR( 1000 ) NULL AFTER  `dfread2` ;