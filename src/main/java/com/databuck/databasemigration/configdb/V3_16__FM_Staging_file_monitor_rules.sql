/* --------------- Creating table for staging_dbk_file_monitor_rules -------------------------------- */

create table if not exists `staging_dbk_file_monitor_rules` (
  Id int(11) NOT NULL AUTO_INCREMENT,
  connection_id int(11) NOT NULL,
  validation_id int(11) NOT NULL,
  schema_name varchar(250) NOT NULL,
  table_name varchar(1000) NOT NULL,
  file_indicator varchar(50) NOT NULL,
  dayOfWeek varchar(50) NOT NULL,
  hourOfDay smallint NULL,
  expected_time smallint NULL,
  expected_file_count smallint NULL,
  start_hour smallint DEFAULT NULL,
  end_hour smallint DEFAULT NULL,
  frequency smallint NULL,
  rule_delta_type varchar(50) NULL,
  PRIMARY KEY (Id)
) engine=innodb auto_increment=100 default charset=latin1;