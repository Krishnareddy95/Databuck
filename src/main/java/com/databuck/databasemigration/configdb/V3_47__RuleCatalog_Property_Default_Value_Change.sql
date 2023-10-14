drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin

	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;
	
	update databuck_property_details set property_value='Y', property_default_value='Y' where property_category_id = (select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='isRuleCatalogDiscovery';

	commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
