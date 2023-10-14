/* --------------- Creating table for run_global_filter_validate_tasks ------------------------------- */

DROP TABLE IF EXISTS run_global_filter_validate_tasks;

CREATE SEQUENCE IF NOT EXISTS run_global_filter_validate_tasks_seq;

CREATE TABLE IF NOT EXISTS run_global_filter_validate_tasks (
  id int NOT NULL DEFAULT NEXTVAL ('run_global_filter_validate_tasks_seq'),
  unique_id varchar(1000),
  process_id bigint DEFAULT NULL,
  spark_app_id varchar(1000) DEFAULT NULL,
  deploy_mode varchar(1000) DEFAULT NULL,
  start_time timestamp(0) DEFAULT NULL,
  end_time timestamp(0) DEFAULT NULL,
  triggered_by varchar(1000) DEFAULT NULL,
  triggered_by_host varchar(1000) DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  execution_percentage int DEFAULT '0',
  is_filter_query_valid boolean DEFAULT false,
  execution_errors text DEFAULT NULL,
  id_data int DEFAULT NULL,
  filter_id int DEFAULT NULL,
  filter_condition text DEFAULT NULL,
  PRIMARY KEY (id)
) ;
ALTER SEQUENCE run_global_filter_validate_tasks_seq RESTART WITH 1;
