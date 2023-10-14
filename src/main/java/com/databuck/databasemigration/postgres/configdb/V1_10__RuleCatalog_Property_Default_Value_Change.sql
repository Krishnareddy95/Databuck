
drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

	declare sSelectedDatabase varchar(100) := (select current_database());
	
begin

		update databuck_property_details set property_value='Y', property_default_value='Y' where property_category_id = (select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='isRuleCatalogDiscovery';

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;