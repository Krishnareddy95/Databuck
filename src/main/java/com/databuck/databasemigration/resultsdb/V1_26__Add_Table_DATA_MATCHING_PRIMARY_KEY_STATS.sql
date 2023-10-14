/* --------------- Creating table for DATA_MATCHING_PRIMARY_KEY_STATS -------------------------------- */

create table if not exists `DATA_MATCHING_PRIMARY_KEY_STATS`(
  id int(11) NOT NULL AUTO_INCREMENT,
  idApp int(11) NOT NULL,
  Date date NOT NULL,
  Run int(11) NOT NULL,
  match_value_left_column varchar(2000),
  match_value_right_column varchar(2000),
  match_percentage double NOT NULL,
  primary key(id)
  )engine=innodb auto_increment=100 default charset=latin1;