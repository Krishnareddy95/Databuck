/*
	26-July-2021 Temporary flag until all download CSV new enhancements are completed in all or main Dev and BE code branch.
	Once code merge and feature get stabilized across all customers, this flag will remain in DB but will not be referred in code.
*/
set @sNewPropertyName = 'isDateRange_DownloadCsv_Enhancements';
set @nAppDbCategoryRowId = (select property_category_id from databuck_properties_master where lower(property_category_name) = 'appdb');
set @nNewPropertyRowId = (select property_id from databuck_property_details where property_category_id = @nAppDbCategoryRowId and property_name = @sNewPropertyName);

select ifnull(@nNewPropertyRowId, -1) into @nNewPropertyRowId;

start transaction;

insert into databuck_property_details (
	property_category_id, property_name, property_value, description, 
	is_mandatory_field, is_password_field, is_value_encrypted, property_default_value, property_data_type, prop_requires_restart, 
	last_updated_at
)
select @nAppDbCategoryRowId, @sNewPropertyName, 'N','Temporary flag until all download CSV new enhancements are completed',
	'N','N','N','N','string','N',
	now()
from databuck_property_details	
where property_id <> @nNewPropertyRowId 
and   @nNewPropertyRowId = -1
limit 1;
	
commit;