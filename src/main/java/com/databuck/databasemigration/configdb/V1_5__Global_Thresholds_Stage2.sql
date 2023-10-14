/* 
	Schema changes included in this file:
	(a) global threshold AbbVie requirements cover (at least POC level) 
	(b) Repair for 'rollTargetSchemaId' need be in 'listApplications' instead of 'listDataAccess' 
*/
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 
	
	create table if not exists SynonymLibrary (
	  synonyms_Id int(11) NOT NULL AUTO_INCREMENT,
	  domain_Id int(11) NOT NULL,
	  tableColumn varchar(200) DEFAULT NULL,
	  possiblenames varchar(200) DEFAULT NULL,
	  PRIMARY KEY (synonyms_Id),
	  UNIQUE KEY SynonymsName (domain_Id,tableColumn)
	) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;

	/* Table listApplications => Tidy up of last changes if exists it does nothing else add column */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'thresholdsapplyoption') then	
		alter table listApplications
			add column thresholdsApplyOption int(11) not null default 0;
	end if;
	
	/* Table listGlobalThresholds => Changes as per redesign or pending portions it does it safely if exists manner */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholds' and lower(column_name) = 'lengthcheckthreshold') then	
		alter table listGlobalThresholds
			add column lengthCheckThreshold double not null default 0;
	end if;

	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholds' and lower(column_name) = 'baddatacheckthreshold') then	
		alter table listGlobalThresholds
			add column badDataCheckThreshold double not null default 0;
	end if;
	
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholds' and lower(column_name) = 'patterncheckthreshold') then	
		alter table listGlobalThresholds
			add column patternCheckThreshold double not null default 0;
	end if;

	/* Table SynonymLibrary => No primary key was present so adding it and making it auto increment */
	if not exists (
		select 1 from information_schema.statistics 
		where table_schema = sSelectedDatabase
		and   lower(table_name) = 'synonymlibrary'
		and   lower(index_name) = 'primary'
	) then
		alter table SynonymLibrary modify synonyms_Id int(11) not null primary key auto_increment; 
	end if;	
	
	if not exists (
		select 1 from information_schema.statistics 
		where table_schema = sSelectedDatabase
		and   lower(table_name) = 'synonymlibrary'
		and   lower(index_name) = 'synonymsname'
	) then
		create unique index SynonymsName on SynonymLibrary (domain_Id, tableColumn);
	end if;	

	/* Table listGlobalThresholdsSelected => Drop all 5 columns added to keep copies of thresholds we do not need copies of all thresholds here */
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholdsselected' and lower(column_name) = 'nullcountthreshold') then	
		alter table listGlobalThresholdsSelected
			drop column nullCountThreshold;
	end if;

	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholdsselected' and lower(column_name) = 'numericalthreshold') then	
		alter table listGlobalThresholdsSelected
			drop column numericalThreshold;
	end if;
	
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholdsselected' and lower(column_name) = 'stringstatthreshold') then	
		alter table listGlobalThresholdsSelected
			drop column stringStatThreshold;
	end if;
	
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholdsselected' and lower(column_name) = 'datadriftthreshold') then	
		alter table listGlobalThresholdsSelected
			drop column dataDriftThreshold;
	end if;
	
	if exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listglobalthresholdsselected' and lower(column_name) = 'recordanomalythreshold') then	
		alter table listGlobalThresholdsSelected
			drop column recordAnomalyThreshold;
	end if;	
	
	/* (b) Repair changes */
	if not exists (select 1 from information_schema.columns where table_schema = sSelectedDatabase and lower(table_name) = 'listapplications' and lower(column_name) = 'rolltargetschemaid') then	
		alter table listApplications
			add column rollTargetSchemaId int(11) default null;
	end if;	
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;

