/* --------------------- Creating table for Custom Microsegments -------------------------------- */

DROP TABLE IF EXISTS DATA_QUALITY_Custom_Column_Summary;

DROP SEQUENCE IF EXISTS DATA_QUALITY_Custom_Column_Summary_seq CASCADE;

CREATE SEQUENCE IF NOT EXISTS DATA_QUALITY_Custom_Column_Summary_seq;

CREATE TABLE IF NOT EXISTS DATA_QUALITY_Custom_Column_Summary (
    Id bigint NOT NULL DEFAULT NEXTVAL ('DATA_QUALITY_Custom_Column_Summary_seq'),
    idApp int DEFAULT NULL,
    Date date DEFAULT NULL,
    Run bigint DEFAULT NULL,
    dayOfYear int DEFAULT NULL,
    month varchar(50) DEFAULT NULL,
    dayOfMonth int DEFAULT NULL,
    dayOfWeek varchar(50) DEFAULT NULL,
    hourOfDay int DEFAULT NULL,
    ColName varchar(255) DEFAULT NULL,
    Count bigint DEFAULT NULL,
    Min double precision DEFAULT NULL,
    Max double precision DEFAULT NULL,
    Cardinality int DEFAULT NULL,
    Std_Dev double precision DEFAULT NULL,
    Mean double precision DEFAULT NULL,
    Null_Value bigint DEFAULT NULL,
    Status varchar(20) DEFAULT NULL,
    StringCardinalityAvg double precision DEFAULT NULL,
    StringCardinalityStdDev double precision DEFAULT NULL,
    StrCardinalityDeviation double precision DEFAULT NULL,
    String_Threshold double precision DEFAULT NULL,
    String_Status varchar(50) DEFAULT NULL,
    NumMeanAvg double precision DEFAULT NULL,
    NumMeanStdDev double precision DEFAULT NULL,
    NumMeanDeviation double precision DEFAULT NULL,
    NumMeanThreshold double precision DEFAULT NULL,
    NumMeanStatus varchar(50) DEFAULT NULL,
    NumSDAvg double precision DEFAULT NULL,
    NumSDStdDev double precision DEFAULT NULL,
    NumSDDeviation double precision DEFAULT NULL,
    NumSDThreshold double precision DEFAULT NULL,
    NumSDStatus varchar(50) DEFAULT NULL,
    outOfNormStatStatus varchar(100) DEFAULT NULL,
    sumOfNumStat double precision DEFAULT NULL,
    NumSumAvg double precision DEFAULT NULL,
    NumSumStdDev double precision DEFAULT NULL,
    NumSumThreshold double precision DEFAULT NULL,
    dGroupVal varchar(255) DEFAULT NULL,
    dGroupCol varchar(255) DEFAULT NULL,
    dataDriftCount bigint DEFAULT NULL,
    dataDriftStatus varchar(50) DEFAULT NULL,
    Default_Value bigint DEFAULT NULL,
    Default_Count bigint DEFAULT NULL,
    Record_Count bigint DEFAULT NULL,
    Null_Percentage double precision DEFAULT NULL,
    Default_Percentage double precision DEFAULT NULL,
    Null_Threshold double precision DEFAULT NULL,
    Default_Threshold double precision DEFAULT NULL,
    forgot_run_enabled varchar(10) DEFAULT 'N',
    PRIMARY KEY (Id)
  )  ;

ALTER SEQUENCE DATA_QUALITY_Custom_Column_Summary_seq RESTART WITH 256;

CREATE INDEX idApp_index ON DATA_QUALITY_Custom_Column_Summary (idApp);
CREATE INDEX Date_index ON DATA_QUALITY_Custom_Column_Summary (Date);
CREATE INDEX dayOfWeek_index ON DATA_QUALITY_Custom_Column_Summary (dayOfWeek);
CREATE INDEX ColName_index ON DATA_QUALITY_Custom_Column_Summary (ColName);
CREATE INDEX dGroupVal_index ON DATA_QUALITY_Custom_Column_Summary (dGroupVal);


