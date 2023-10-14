/* --------------- Creating tables for Business Attribute -------------------------------- */

DROP TABLE IF EXISTS rule_business_attribute_mapping;

CREATE SEQUENCE IF NOT EXISTS rule_business_attribute_mapping_seq;
CREATE TABLE IF NOT EXISTS rule_business_attribute_mapping(
  id INT NOT NULL DEFAULT NEXTVAL ('rule_business_attribute_mapping_seq'),
  business_attribute_id int,
  idApp bigint,
  rule_id int,
  PRIMARY KEY (id)

) ;
ALTER SEQUENCE rule_business_attribute_mapping_seq RESTART WITH 1;
