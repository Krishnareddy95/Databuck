/* ------------------- Creating table for Custom Microsegments -------------------------------- */

DROP TABLE IF EXISTS custom_microsegment_details;

DROP SEQUENCE IF EXISTS custom_microsegment_details_seq CASCADE;

CREATE SEQUENCE IF NOT EXISTS custom_microsegment_details_seq;

CREATE TABLE IF NOT EXISTS custom_microsegment_details (
  id INT NOT NULL DEFAULT NEXTVAL ('custom_microsegment_details_seq'),
  template_id INT NOT NULL,
  check_name VARCHAR(255) NULL,
  microsegment_columns VARCHAR(2500) NULL,
  check_columns VARCHAR(2500) NULL,
  PRIMARY KEY (id)
)  ;
ALTER SEQUENCE custom_microsegment_details_seq RESTART WITH 256;