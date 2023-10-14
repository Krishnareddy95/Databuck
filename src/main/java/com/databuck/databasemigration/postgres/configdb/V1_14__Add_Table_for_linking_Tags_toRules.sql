/* --------------- Creating tables for Databuck Tags -------------------------------- */

DROP TABLE IF EXISTS global_rule_tag_mapping;

CREATE SEQUENCE IF NOT EXISTS rule_tag_mapping_seq;
CREATE TABLE IF NOT EXISTS rule_tag_mapping(
  id INT NOT NULL DEFAULT NEXTVAL ('rule_tag_mapping_seq'),
  tag_id int,
  idApp bigint,
  rule_id int,
  PRIMARY KEY (id)

) ;
ALTER SEQUENCE rule_tag_mapping_seq RESTART WITH 1;


