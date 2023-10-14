/* --------------- Creating table for project_jobs_queue -------------------------------- */

create table if not exists `domain_jobs_queue` (
  queueId int(11) NOT NULL AUTO_INCREMENT,
  domainId int(11) NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt datetime DEFAULT NULL,
  deployMode varchar(250) DEFAULT NULL,
  processId bigint(20) DEFAULT NULL,
  sparkAppId varchar(1000) DEFAULT NULL,
  startTime datetime DEFAULT NULL,
  endTime datetime DEFAULT NULL,
  PRIMARY KEY (queueId)
) engine=innodb auto_increment=100 default charset=latin1;


/* --------------- Creating table for project_jobs_queue -------------------------------- */

create table if not exists `domain_jobs_tracking` (
  id int(11) NOT NULL AUTO_INCREMENT,
  domainId int(11) NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  projectId int(11) NOT NULL,
  project_uniqueId varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id)
) engine=innodb auto_increment=100 default charset=latin1;