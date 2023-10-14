/* creating new table to store multiPattern template info */

DROP TABLE IF EXISTS `schema_multipattern_info`;

CREATE TABLE IF NOT EXISTS `schema_multipattern_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDataSchema` bigint(20) NOT NULL,
  `idData` bigint(20) NOT NULL,
  `filePattern` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/* Existing table 'listDataSchema' - Add columns for FileMonitor MultiPattern  */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'multipattern') then	
		alter table listDataSchema
			add column multiPattern varchar(10) default 'N';
	end if;	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'startinguniquecharcount') then	
		alter table listDataSchema
			add column startingUniqueCharCount int(10)  default 0;
	end if;	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'endinguniquecharcount') then	
		alter table listDataSchema
			add column endingUniqueCharCount int(10)  default 0;
	end if;	
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listdataschema' and lower(column_name) = 'maxfolderdepth') then	
		alter table listDataSchema
			add column maxFolderDepth int(10) default 2;
	end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
