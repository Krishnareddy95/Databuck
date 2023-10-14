/* --------------- Creating table for domain_lite_jobs_queue -------------------------------- */

create table if not exists `domain_lite_jobs_queue` (
  queueId int(11) NOT NULL AUTO_INCREMENT,
  domainId int(11) NOT NULL,
  uniqueId varchar(2500) NOT NULL,
  triggeredByHost varchar(2500) DEFAULT NULL,
  status varchar(500) DEFAULT NULL,
  createdAt datetime DEFAULT NULL,
  startTime datetime DEFAULT NULL,
  endTime datetime DEFAULT NULL,
  resultJson text DEFAULT NULL,
  PRIMARY KEY (queueId)
) engine=innodb auto_increment=100 default charset=latin1;
