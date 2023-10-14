/* --------------- Creating tables for Databuck Tags -------------------------------- */

CREATE SEQUENCE IF NOT EXISTS databuck_tags_seq;

CREATE TABLE IF NOT EXISTS databuck_tags (
  tag_id INT NOT NULL DEFAULT NEXTVAL ('databuck_tags_seq'),
  tag_name VARCHAR(1000) ,
  description VARCHAR(2500) ,
  PRIMARY KEY (tag_id)
) ;

ALTER SEQUENCE databuck_tags_seq RESTART WITH 1;


CREATE SEQUENCE IF NOT EXISTS global_rule_tag_mapping_seq;

CREATE TABLE IF NOT EXISTS global_rule_tag_mapping (
  id INT NOT NULL DEFAULT NEXTVAL ('global_rule_tag_mapping_seq'),
  tag_id INT ,
  global_rule_id INT ,
  PRIMARY KEY (id)
) ;

ALTER SEQUENCE global_rule_tag_mapping_seq RESTART WITH 1;
