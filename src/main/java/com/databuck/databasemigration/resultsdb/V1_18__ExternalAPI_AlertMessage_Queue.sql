/* --------------- Create a table to store the external API alert messages --------------- */

DROP TABLE IF EXISTS `external_api_alert_msg_queue`;

create table if not exists external_api_alert_msg_queue (
id int(11) not null primary key auto_increment,
external_api_type varchar(100) not null,
taskType  varchar(2500) not null,
taskId int(11) not null,
uniqueId varchar(2500) not null,
execution_date date,
run int(11),
test_run varchar(20) default 'N',
alter_timeStamp datetime,
alert_msg varchar(2500),
alert_msg_code varchar(2500),
alert_label varchar(2500),
alert_json  text,
alert_msg_deliver_status varchar(2500)
) engine=innodb auto_increment=1 default charset=latin1; 



