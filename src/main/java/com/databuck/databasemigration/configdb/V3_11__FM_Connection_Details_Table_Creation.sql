create table if not exists `fm_connection_details` (
  Id int(11) NOT NULL AUTO_INCREMENT,
  idApp int(11) NOT NULL,
  idDataSchema int(11) NOT NULL,
  last_load_time datetime DEFAULT NULL,
   PRIMARY KEY (Id)
) engine=innodb auto_increment=100 default charset=latin1;




