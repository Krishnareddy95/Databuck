drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='consolidated.status.check.enabled') then
    	update databuck_property_details set description='To display root cause analysis and exceptions enter "Y", else "N"' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
    	and property_name='consolidated.status.check.enabled';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='unique.partitionkey.fieldname') then
        update databuck_property_details set description='Column name of unique partition key' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='unique.partitionkey.fieldname';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='download.failed.data.reports.allowed') then
        update databuck_property_details set description='To allow failed data reports download enter "Y", else "N"' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='download.failed.data.reports.allowed';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='default.pattern.threshold') then
        update databuck_property_details set description='Min frequency of occurrence of a pattern to be recognized as an acceptable pattern (in %). Eg., 7' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='default.pattern.threshold';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='incremental.lastread.column.name') then
        update databuck_property_details set description='Date and/or time column used to identify the last read-time' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='incremental.lastread.column.name';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='auto.incremental.validation.creation.enabled') then
        update databuck_property_details set description='For automatic incremental validation creation, enter "Y" else "N"' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='auto.incremental.validation.creation.enabled';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='azure_result_directory') then
        update databuck_property_details set description='Directory where Azure results are stored' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='azure_result_directory';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='completeness.exclusion.special.columns') then
        update databuck_property_details set description='Column names in which specified keywords should be considered as Nulls, eg., Col-A, Col-B, ….' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='completeness.exclusion.special.columns';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='completeness.special.columns.exclusion.keywords') then
        update databuck_property_details set description='These keywords should be considered as Nulls for the specified columns, eg., Blank, N/A,…' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='completeness.special.columns.exclusion.keywords';
    end if;

    if exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='completeness.exclusion.keywords') then
        update databuck_property_details set description='These keywords should be considered as Nulls for all Null check enabled columns, eg., Blank, N/A,…' where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb')
        and property_name='completeness.exclusion.keywords';
    end if;



	commit;
end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;