/* --------------- Creating tables for Business Attribute -------------------------------- */

CREATE TABLE IF NOT EXISTS rule_business_attribute_mapping(
  id INT NOT NULL AUTO_INCREMENT,
  business_attribute_id int(11),
  idApp bigint(20),
  rule_id int(11),
  PRIMARY KEY (id)

)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
