drop table if exists databuck_login_access_logs;

create table if not exists databuck_login_access_logs (
	row_id					int(11) not null auto_increment primary key,
	user_id					int(11) null,
	user_name				varchar(500) not null,
	access_url				varchar(1000) not null,
	databuck_feature		varchar(255) null,
	session_id				varchar(255) null,
	activity_log_time		varchar(40) not null,
	login_status         	varchar(10) not null)
	 engine=innodb auto_increment=1 default charset=latin1;