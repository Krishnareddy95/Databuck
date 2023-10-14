/* --------------- Creating table to act as SNS message QUEUE -------------------------------- */
DROP TABLE IF EXISTS `DQ_SNS_MSG_QUEUE`;

CREATE TABLE IF NOT EXISTS `DQ_SNS_MSG_QUEUE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `msg_id` text,
  `sns_msg_body` text,
  `sns_alert_enabled` varchar(10) DEFAULT 'N',
  `sns_alert_sent` varchar(10) DEFAULT 'N',
  `createdAt` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;