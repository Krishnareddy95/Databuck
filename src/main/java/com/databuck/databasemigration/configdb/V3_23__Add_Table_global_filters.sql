/*  Adding new Table global_filters to manage filter condition at global level  */

create table if not exists global_filters(
  global_filter_id int(11) NOT NULL AUTO_INCREMENT,
  global_filter_name varchar(255),
  description varchar(255),
  global_filter_condition text,
  createdAt datetime,
  domain_id int(11),
  PRIMARY KEY (global_filter_id)
) engine=innodb auto_increment=1 default charset=latin1;
