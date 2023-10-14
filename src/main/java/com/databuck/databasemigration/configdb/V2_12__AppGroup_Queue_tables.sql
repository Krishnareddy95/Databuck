/* --------------- Creating table for appgroup_jobs_queue -------------------------------- */
DROP TABLE IF EXISTS `appgroup_jobs_queue`;

create table if not exists appgroup_jobs_queue (
   queueId int(11) not null primary key auto_increment,
   idAppGroup int(11) not null,   
   uniqueId varchar(2000) not null, 
   status varchar(500),
   createdAt datetime
) engine=innodb auto_increment=1 default charset=latin1;


/* --------------- Creating table for appgroup_jobs_tracking -------------------------------- */
DROP TABLE IF EXISTS `appgroup_jobs_tracking`;

create table if not exists appgroup_jobs_tracking (
   id int(11) not null primary key auto_increment,
   idAppGroup int(11) not null,   
   uniqueId varchar(2000) not null, 
   idApp int(11) not null,
   validation_uniqueId varchar(2000)
) engine=innodb auto_increment=1 default charset=latin1;