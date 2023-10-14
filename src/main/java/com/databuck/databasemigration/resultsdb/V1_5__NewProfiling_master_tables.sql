/* --------------- Adding columns for column_profile_master_table - alter table add column -------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	/* It is safer to add multiple columns one by one, as each column wise as "not exists integrity" is checked per column */
	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'column_profile_master_table' and lower(COLUMN_NAME) = 'idData') then	
		alter table column_profile_master_table
			add column idData bigint(20);
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

/* 
   Schema changes for adding column_profile_detail_master_table in databuck results DB 
*/
drop table if exists column_profile_detail_master_table;

create table if not exists column_profile_detail_master_table(
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idData bigint(20),
    idDataSchema bigint(20),
    folderPath text,
    table_or_fileName text,
    Column_Name varchar(1000),
    Column_Value varchar(1000),
    Count bigint(20),
    Percentage decimal(5,2)
)ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1; 

/* 
   Schema changes for adding column_combination_profile_master_table in databuck results DB 
*/
drop table if exists column_combination_profile_master_table;

create table if not exists column_combination_profile_master_table(
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idData bigint(20),
    idDataSchema bigint(20),
    folderPath text,
    table_or_fileName text,
    Column_Group_Name varchar(1000),
    Column_Group_Value varchar(1000),
    Count bigint(20),
    Percentage decimal(5,2)
)ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1; 

/* 
   Schema changes for adding row_profile_master_table in databuck results DB 
*/
drop table if exists row_profile_master_table;

create table if not exists row_profile_master_table(
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idData bigint(20),
    idDataSchema bigint(20),
    folderPath text,
    table_or_fileName text,
    Number_of_Columns_with_NULL bigint(20),
    Number_of_Records bigint(20),
    Percentage_Missing decimal(5,2)

)ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1; 

/* 
   Schema changes for adding numerical_profile_master_table in databuck results DB 
*/
drop table if exists numerical_profile_master_table;

create table if not exists numerical_profile_master_table(
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    idData bigint(20),
    idDataSchema bigint(20),
    folderPath text,
    table_or_fileName text,
    Column_Name_1 varchar(1000),
    Column_Name_2 varchar(1000),
    Correlation decimal(5,2)
)ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;                                                                          


