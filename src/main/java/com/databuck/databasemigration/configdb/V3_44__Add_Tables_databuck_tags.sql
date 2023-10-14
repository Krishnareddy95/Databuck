/* --------------- Creating tables for Databuck Tags -------------------------------- */

CREATE TABLE IF NOT EXISTS databuck_tags (
  tag_id INT NOT NULL AUTO_INCREMENT,
  tag_name VARCHAR(1000) ,
  description VARCHAR(2500) ,
  PRIMARY KEY (tag_id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS global_rule_tag_mapping (
  id INT NOT NULL AUTO_INCREMENT,
  tag_id INT ,
  global_rule_id INT ,
  PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
