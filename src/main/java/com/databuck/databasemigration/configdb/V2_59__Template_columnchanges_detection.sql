/* creating new table to store template column change details */

DROP TABLE IF EXISTS `template_column_change_history`;

CREATE TABLE IF NOT EXISTS `template_column_change_history` (
  `templateId` bigint(20) NOT NULL,
  `columnName` varchar(2500) NOT NULL,
  `isNewColumn` varchar(10),
  `isMissingColumn` varchar(10),
  `changeDetectedTime` datetime,
  CONSTRAINT pk_template_column_change_history PRIMARY KEY (`templateId`,`columnName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;