/* create table dbk_fm_load_history_table in databuck results DB */
DROP TABLE IF EXISTS `dbk_fm_load_history_table`;

create table if not exists `dbk_fm_load_history_table` (
Id int(11) NOT NULL AUTO_INCREMENT,
connection_id int(11) NOT NULL,
validation_id int(11) NOT NULL,
connection_type varchar(100) NULL,
schema_name varchar(250) NOT NULL,
table_name varchar(1000) NOT NULL,
record_count int(11) NULL,
last_load_time datetime(3) DEFAULT NULL,
last_altered datetime(3) DEFAULT NULL,
PRIMARY KEY (Id)
) engine=innodb auto_increment=100 default charset=latin1;

/*  create table dbk_fm_filearrival_details in databuck results DB  */
DROP TABLE IF EXISTS `dbk_fm_filearrival_details`;

create table if not exists `dbk_fm_filearrival_details` (
Id int(11) NOT NULL AUTO_INCREMENT,
connection_id int(11) NOT NULL,
validation_id int(11) NOT NULL,
schema_name varchar(250) NOT NULL,
table_or_file_name varchar(1000) NOT NULL,
file_indicator varchar(50) NULL,
dayOfWeek varchar(50) NOT NULL,
load_date date DEFAULT NULL,
loaded_hour smallint NULL,
loaded_time smallint NULL,
size_or_record_count int(11) NOT NULL,
size_or_record_count_check varchar(50),
column_metadata_check varchar(50),
file_validity_status varchar(50),
file_arrival_status varchar(50),
PRIMARY KEY (Id)
) engine=innodb auto_increment=100 default charset=latin1;

/*  create table dbk_fm_summary_details in databuck results DB  */

DROP TABLE IF EXISTS `dbk_fm_summary_details`;

create table if not exists `dbk_fm_summary_details` (
Id int(11) NOT NULL AUTO_INCREMENT,
connection_id int(11) NOT NULL,
validation_id int(11) NOT NULL,
schema_name varchar(250) NOT NULL,
table_or_file_name varchar(1000) NOT NULL,
file_indicator varchar(50) NULL,
dayOfWeek varchar(50) NOT NULL,
load_date date DEFAULT NULL,
loaded_hour smallint NULL,
expected_minute smallint NULL,
actual_file_count smallint NULL,
expected_file_count smallint NULL,
status varchar(50),
PRIMARY KEY (Id)
) engine=innodb auto_increment=100 default charset=latin1;

/*  create table dbk_fm_audit_details in databuck results DB  */

DROP TABLE IF EXISTS `dbk_fm_audit_details`;

create table if not exists `dbk_fm_audit_details` (
fm_activity_Id int(11) NOT NULL AUTO_INCREMENT,
fm_activity_name varchar(500),
execution_date date DEFAULT NULL,
execution_start_time datetime(3) NULL,
execution_end_time datetime(3) NULL,
status varchar(1000) NOT NULL,
PRIMARY KEY (fm_activity_Id)
)engine=innodb auto_increment=100 default charset=latin1;