ALTER TABLE `task_progress_status`
 DROP `regex`,
  DROP `orphan`,
  DROP `referential`,
  DROP `crossreferential`;
  ALTER TABLE `task_progress_status` ADD `rules` VARCHAR( 10 ) NULL DEFAULT NULL AFTER `datadrift` ;