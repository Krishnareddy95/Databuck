/* ---------Creating table DATA_QUALITY_Duplicate_Check_Summary to provide dGroup summary feature in duplicate check ----------- */

create table if not exists DATA_QUALITY_Duplicate_Check_Summary(
  Id int(11) NOT NULL AUTO_INCREMENT,
  idApp int(11),
  Date date ,
  Run bigint(20),
  Type varchar(50),
  dGroupCol text,
  dGroupVal text,
  duplicateCheckFields varchar(250),
  Duplicate bigint(20),
  TotalCount bigint(20),
  Percentage double,
  Threshold double,
  Status varchar(50),
  PRIMARY KEY (Id)
) engine=innodb auto_increment=1 default charset=latin1;