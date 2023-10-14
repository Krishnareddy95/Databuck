/* --------------- Creating table for project_jobs_queue -------------------------------- */
DROP TABLE IF EXISTS `project_jobs_queue`;

create table if not exists project_jobs_queue (
   queueId int(11) not null primary key auto_increment,
   projectId int(11) not null,   
   uniqueId varchar(2500) not null, 
   triggeredByHost varchar(2500),
   status varchar(500),
   createdAt datetime
) engine=innodb auto_increment=1 default charset=latin1;


/* --------------- Creating table for project_jobs_tracking -------------------------------- */
DROP TABLE IF EXISTS `project_jobs_tracking`;

create table if not exists project_jobs_tracking (
   id int(11) not null primary key auto_increment,
   projectId int(11) not null,   
   uniqueId varchar(2500) not null, 
   idDataSchema int(11) not null,
   connection_uniqueId varchar(2000)
) engine=innodb auto_increment=1 default charset=latin1;