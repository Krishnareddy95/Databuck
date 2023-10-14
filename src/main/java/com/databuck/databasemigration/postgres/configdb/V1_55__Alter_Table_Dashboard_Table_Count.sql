-- Existing table 'dashboard_table_count' - Modify column hours_saved

-- Drop the function if it already exists
DROP FUNCTION IF EXISTS dummy_do_not_use();

-- Create the function
CREATE OR REPLACE FUNCTION dummy_do_not_use() RETURNS void AS $$
DECLARE
    sSelectedDatabase varchar(100) := '';
BEGIN
    SELECT current_database() INTO sSelectedDatabase;

    -- Check if the column exists and modify if needed
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE lower(table_name) = 'dashboard_table_count'
          AND lower(column_name) = 'hours_saved'
    ) THEN
        ALTER TABLE dashboard_table_count
        ALTER COLUMN hours_saved TYPE VARCHAR(50),
        ALTER COLUMN hours_saved DROP NOT NULL;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Call the function
SELECT dummy_do_not_use();

-- Drop the function
DROP FUNCTION IF EXISTS dummy_do_not_use();