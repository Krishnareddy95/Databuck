/* --------------- Adding dGroup columns inside table DATA_QUALITY_GlobalRules-------------------------------- */
drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';	
	select database() into sSelectedDatabase; 

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_globalrules' and lower(COLUMN_NAME) = 'dgroupval') then
		alter table DATA_QUALITY_GlobalRules add column dGroupVal text AFTER `ruleName`;
	end if;

	if not exists (select 1 from information_schema.columns where TABLE_SCHEMA = sSelectedDatabase and lower(TABLE_NAME) = 'data_quality_globalrules' and lower(COLUMN_NAME) = 'dgroupcol') then
        alter table DATA_QUALITY_GlobalRules add column dGroupCol text AFTER `ruleName`;
    end if;
	
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;