/* --------------- Creating table for ruleMapping -------------------------------- */
DROP TABLE IF EXISTS `ruleMapping`;

create table if not exists `ruleMapping` (
  idruleMap int(11) not null primary key AUTO_INCREMENT,
  viewName varchar(60) not null,
  description varchar(80) DEFAULT null,
  idListColrules varchar(1000) not null,
  idData varchar(1000) not null,
  idApp varchar(1000) not null
) engine=innodb auto_increment=15 default charset=latin1;