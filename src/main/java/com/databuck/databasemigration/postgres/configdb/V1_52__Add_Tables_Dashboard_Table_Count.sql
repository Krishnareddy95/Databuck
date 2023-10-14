-- Create a custom sequence for the id column
CREATE SEQUENCE IF NOT EXISTS dashboard_table_count_seq;

-- Create the table using the custom sequence
CREATE TABLE IF NOT EXISTS dashboard_table_count (
  id INTEGER DEFAULT NEXTVAL('dashboard_table_count_seq') PRIMARY KEY,
  schema_id INTEGER NOT NULL,
  total_table_count INTEGER,
  monitored_table_count INTEGER,
  unvalidated_table_count INTEGER,
  high_trust_table_count INTEGER,
  low_trust_table_count INTEGER,
  rules_executed INTEGER,
  issues_detected INTEGER,
  hours_saved INTEGER,
  updated_date_time TIMESTAMP
);

ALTER SEQUENCE dashboard_table_count_seq RESTART WITH 1;