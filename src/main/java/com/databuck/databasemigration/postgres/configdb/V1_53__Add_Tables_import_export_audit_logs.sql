CREATE SEQUENCE IF NOT EXISTS import_export_audit_logs_seq;

CREATE TABLE IF NOT EXISTS import_export_audit_logs (
  id INTEGER DEFAULT NEXTVAL('import_export_audit_logs_seq') PRIMARY KEY,
  unique_id VARCHAR(1000),
  exported_application_id INTEGER,
  imported_application_id INTEGER,
  task_type VARCHAR(100),
  file_path VARCHAR(1000),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  status VARCHAR(100),
  status_message TEXT,
  error_code VARCHAR(500),
  error_message TEXT,
  created_by INTEGER,
  created_by_user VARCHAR(1000),
  hash VARCHAR(1000)
);

ALTER SEQUENCE import_export_audit_logs_seq RESTART WITH 1;
