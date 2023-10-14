/* --------------- Creating table for run_global_filter_validate_tasks ------------------------------- */

CREATE TABLE IF NOT EXISTS run_global_filter_validate_tasks (
  id int(11) NOT NULL AUTO_INCREMENT,
  unique_id varchar(1000),
  process_id bigint(20) DEFAULT NULL,
  spark_app_id varchar(1000) DEFAULT NULL,
  deploy_mode varchar(1000) DEFAULT NULL,
  start_time datetime DEFAULT NULL,
  end_time datetime DEFAULT NULL,
  triggered_by varchar(1000) DEFAULT NULL,
  triggered_by_host varchar(1000) DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  execution_percentage int DEFAULT '0',
  is_filter_query_valid boolean DEFAULT false,
  execution_errors text DEFAULT NULL,
  id_data int(11) DEFAULT NULL,
  filter_id int(11) DEFAULT NULL,
  filter_condition text DEFAULT NULL,
  PRIMARY KEY (id)
)ENGINE=InnoDB  DEFAULT CHARSET=latin1;
