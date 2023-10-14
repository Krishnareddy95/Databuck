/* 
   Schema changes for column_profile_master_table in databuck results DB 
*/
create table if not exists column_profile_master_table(
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idDataSchema bigint(20),
    folderPath text,
    table_or_fileName text,
    Column_Name varchar(1000),
    Data_Type  varchar(1000),
    Total_Record_Count bigint(20),
    Missing_Value bigint(20),
    Percentage_Missing decimal(5,2),
    Unique_Count bigint(20),
    Min_Length int,
    Max_Length int,
    Mean varchar(1000),
    Std_Dev varchar(1000),
    Min varchar(1000),
    Max varchar(1000),
    99_percentaile varchar(1000),
    75_percentile varchar(1000),
    25_percentile varchar(1000),
    1_percentile  varchar(1000)
)ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1; 
                                                                         


