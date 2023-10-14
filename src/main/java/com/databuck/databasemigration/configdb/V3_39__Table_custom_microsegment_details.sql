/* --------------- Creating table for Custom Microsegments -------------------------------- */
CREATE TABLE IF NOT EXISTS custom_microsegment_details (
  id INT(11) NOT NULL AUTO_INCREMENT,
  template_id INT(11) NOT NULL,
  check_name VARCHAR(255) NULL,
  microsegment_columns VARCHAR(2500) NULL,
  check_columns VARCHAR(2500) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=256 DEFAULT CHARSET=utf8;