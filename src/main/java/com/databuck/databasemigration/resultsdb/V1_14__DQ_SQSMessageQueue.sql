/* --------------- Creating table to act as SQS message QUEUE -------------------------------- */
DROP TABLE IF EXISTS `DQ_SQS_MSG_QUEUE`;

CREATE TABLE IF NOT EXISTS `DQ_SQS_MSG_QUEUE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uniqueId` varchar(1000) NOT NULL UNIQUE,
  `idApp` bigint(20) NOT NULL,
  `execution_date` date DEFAULT NULL,
  `run` bigint(20) DEFAULT NULL,
  `sqs_alert_enabled` varchar(10) DEFAULT 'N',
  `sqs_alert_sent` varchar(10) DEFAULT 'N',
  `createdAt` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;