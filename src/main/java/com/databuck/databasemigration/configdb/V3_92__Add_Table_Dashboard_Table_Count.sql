CREATE TABLE IF NOT EXISTS `dashboard_table_count` (
  `id` int NOT NULL AUTO_INCREMENT,
  `schema_id` int NOT NULL,
  `total_table_count` long DEFAULT NULL,
  `monitored_table_count` long DEFAULT NULL,
  `unvalidated_table_count` long DEFAULT NULL,
  `high_trust_table_count` long DEFAULT NULL,
  `low_trust_table_count` long DEFAULT NULL,
  `rules_executed` long DEFAULT NULL,
  `issues_detected` long DEFAULT NULL,
  `hours_saved` DECIMAL(10, 2) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
