/* --------------- Creating tables for Databuck Tags -------------------------------- */

CREATE TABLE IF NOT EXISTS rule_tag_mapping(
  id INT NOT NULL AUTO_INCREMENT,
  tag_id int(11),
  idApp bigint(20),
  rule_id int(11),
  PRIMARY KEY (id)

)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
